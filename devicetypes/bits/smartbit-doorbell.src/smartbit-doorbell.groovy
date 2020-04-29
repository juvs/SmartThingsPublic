/**
 *  SmartBit Garage
 *
 *  Copyright 2018 JuvsGamer
 *  Version: 1.0.2
 *  Date: 19 MAR 2018
 *      Initial version
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
 
import groovy.json.JsonSlurper
import groovy.util.XmlSlurper

metadata {
	definition (name: "SmartBit Doorbell", namespace: "bits", author: "Juvenal Guzman", ocfDeviceType:"x.com.st.d.doorbell") {
	    capability "Configuration"
    	capability "Health Check"
		capability "Refresh"
		capability "Sensor"
        capability "Polling"
        capability "Notification"
    
		command "reboot"
        
        attribute   "needUpdate", "string"
	}
    
    preferences {
    }    
    
	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"ringing", type: "generic", width: 6, height: 4){
			tileAttribute ("device.ringing", key: "PRIMARY_CONTROL") {
				attributeState "ringing", label:'ringing', icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/juvs/smart-bell-wifi.src/ringing@2x.png", backgroundColor:"#53a7c0"
				attributeState "idle", label:'idle', icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/juvs/smart-bell-wifi.src/sleep@2x.png", backgroundColor:"#ffffff"
			}
		}
        
		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
                      
        valueTile("lastevent", "lastevent", width: 6, height: 2) {
    		state "lastevent", label:'Last event\r\n${currentValue}'
		}        
        
        valueTile("ip", "ip", width: 2, height: 2) {
    		state "ip", label:'IP Address\r\n${currentValue}'
		}
        
        valueTile("uptime", "uptime", width: 2, height: 2) {
    		state "uptime", label:'Up time\r\n${currentValue}'
		}  
	}
    
    main(["ringing"])
	details(["ringing",
    	"lastevent", "refresh", "ip", "uptime"])    
}

def installed() {
	log.debug "installed()"
    refresh()
}

def updated() {
    log.debug "updated(), settings: ${settings}"
	initializeCheckin()
}

def configure() {	
	log.debug "configure()"
    initializeCheckin()
}

private initializeCheckin() {
    //Time in seconds...
    def mins = 10
    // Set the Health Check interval so that it can be skipped once plus 2 minutes.
	def checkInterval = ((mins * 60) + (2 * 60))
    sendEvent(name: "checkInterval", value: checkInterval , displayed: false, data: [protocol: "lan", hubHardwareId: device.hub.hardwareID])
    refresh()
}

private subscribeAction(callbackPath="") {
    def hubip = device.hub.getDataValue("localIP")
    def hubport = device.hub.getDataValue("localSrvPortTCP")
    def hubAction = new physicalgraph.device.HubAction(
        method: "SUBSCRIBE",
        path: "/subscribe",
        headers: [
            HOST: getHostAddress(),
            CALLBACK: "<http://${hubip}:${hubport}/notify$callbackPath>",
            NT: "upnp:event",
            TIMEOUT: "Second-3600"])
            
	//log.trace "Subscribe action : $hubAction"
	return hubAction
}

def refresh() {
	//test()
	log.debug "refresh()"
    def cmds = []
    cmds << subscribeAction()
    cmds << getAction("/status")
    response(cmds)
}

def ping() {
    log.debug "ping()"
    refresh()
}

def poll() {
	log.debug "poll()"
    refresh()
}

def test() {
	log.debug "test() ${device.getDeviceNetworkId()}"
    parent.activateOtherDevices(device.getDeviceNetworkId())
}

// parse events into attributes
def parse(String description) {
	log.debug "parse()"
	def events = []
	//log.debug "Parsing '${description}'"
	def msg = parseLanMessage(description)
    def json = msg.json
   
    log.debug "Lan Message : ${msg}"
    //log.debug "Json : '${json}'"
    
    if (json != null) {
    	if (json.containsKey("uptime")) {
        	events << createEvent(name: "uptime", value: json.uptime, displayed: false)
    	}
        
        if (json.containsKey("action")) {
        	def currentAction = json.action
        	log.debug "Notify action: ${currentAction}"
            events << createEvent(name: "ringing", value: currentAction, descriptionText: "Door bell is $currentAction")
            if (currentAction == "ringing") {
            	events << createEvent(name: "lastevent", value: convertToLocalTimeString(new Date()), displayed: false)
                log.debug "Alguien está tocando a la puerta"
                if (parent.sendNotification != null) {
                	parent.sendNotification("Alguien está tocando a la puerta")
                }
                /*if (parent.turnOnDevicesWhenRinging != null) {
                	parent.turnOnDevicesWhenRinging(device.getDeviceNetworkId())
                }*/
            }
        }
        
        if (json.containsKey("macAddr")) {
            if (!state.mac || state.mac != json.macAddr) {
    			log.debug "Mac address of device found ${json.macAddr}"
        		updateDataValue("mac", json.macAddr)
            }
    	}        
    }
    
	if (state.mac != null && state.dni != state.mac) state.dni = setDeviceNetworkId(state.mac)
    if (!device.currentValue("ip") || (device.currentValue("ip") != getDataValue("ip"))) 
    	events << createEvent(name: 'ip', value: getDataValue("ip"))

    return events
}

def sync(ip, port) {
	log.debug "sync()"
	def existingIp = getDataValue("ip")
	def existingPort = getDataValue("port")
	if (ip && ip != existingIp) {
		updateDataValue("ip", ip)
        sendEvent(name: 'ip', value: ip)
	}
	if (port && port != existingPort) {
		updateDataValue("port", port)
	}
}

private getAction(uri){ 
  updateDNI()
  //def userpass
  //log.debug uri
  //if(password != null && password != "") 
  //  userpass = encodeCredentials("admin", password)
    
  def headers = getHeaders()

  def hubAction = new physicalgraph.device.HubAction(
    method: "GET",
    path: uri,
    headers: headers
  )
  return hubAction    
}

private updateDNI() { 
    if (state.dni != null && state.dni != "" && device.deviceNetworkId != state.dni) {
       device.deviceNetworkId = state.dni
    }
    //log.debug "ip : ${ip}, port: ${port}"
    // Setting Network Device Id
    //def iphex = convertIPtoHex(ip)
    //def porthex = convertPortToHex(port)
    //if (device.deviceNetworkId != "$iphex:$porthex") {
    //    device.deviceNetworkId = "$iphex:$porthex"
    //}
    log.debug "Device Network Id set to ${device.deviceNetworkId}"    
}

private getHeaders(userpass = null){
    def headers = [:]
    headers.put("Host", getHostAddress())
    headers.put("Content-Type", "application/x-www-form-urlencoded")
    if (userpass != null)
       headers.put("Authorization", userpass)
    return headers
}

private setDeviceNetworkId(ip, port = null) {
    def myDNI
    if (port == null) {
        myDNI = ip
    } else {
  	    def iphex = convertIPtoHex(ip)
  	    def porthex = convertPortToHex(port)
        myDNI = "$iphex:$porthex"
    }
    log.debug "Device Network Id set to ${myDNI}"
    return myDNI
}

// gets the address of the Hub
private getCallBackAddress() {
    return device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")
}

private getHostAddress() {
    if (override == "true" && ip != null && ip != ""){
        return "${ip}:80"
    }
    else if(getDeviceDataByName("ip") && getDeviceDataByName("port")){
        return "${getDeviceDataByName("ip")}:${getDeviceDataByName("port")}"
    }else{
	    return "${ip}:80"
    }
}

private Integer convertHexToInt(hex) {
    return Integer.parseInt(hex,16)
}

private String convertHexToIP(hex) {
    return [convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

private String convertIPtoHex(ipAddress) { 
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
    return hex
}

private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04x', port.toInteger() )
    return hexport
}

private convertToLocalTimeString(dt) {
	def timeZoneId = location?.timeZone?.ID
	if (timeZoneId) {
		return dt.format("dd/MMM/yyyy hh:mm a", TimeZone.getTimeZone(timeZoneId))
	}
	else {
		return "$dt"
	}	
}