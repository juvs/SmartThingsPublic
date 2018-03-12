/**
 *  SmartBit Garage
 *
 *  Copyright 2018 JuvsGamer
 *  Version: 1.0.0 
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
metadata {
	definition (name: "SmartBit Garage", namespace: "bits", author: "JuvsGamer") {
    	capability "Garage Door Control"
        capability "Sensor"
        capability "Refresh"
		capability "Configuration"
		capability "Health Check"
		capability "Polling"
        
        command "lock"
        command "override"
        attribute "lockStatus", "string"
        attribute "overrideMode", "string"
	}

    preferences {
    } 

	simulator {
		// TODO: define status and reply messages here
	}
    
	tiles (scale: 2){      
		multiAttributeTile(name:"door", type: "generic", width: 6, height: 4, canChangeIcon: false){
			tileAttribute ("device.door", key: "PRIMARY_CONTROL") {
				attributeState "open", label:'${name}', action:"garageDoorControl.close", backgroundColor:"#e86d13", icon: "st.doors.garage.garage-open", nextState:"closing"
				attributeState "closed", label:'${name}', action:"garageDoorControl.open", backgroundColor:"#00a0dc", icon: "st.doors.garage.garage-closed", nextState:"opening"
				attributeState "opening", label:'${name}', action:"garageDoorControl.close", backgroundColor:"#ffffff", icon: "st.doors.garage.garage-opening", nextState:"closing"
				attributeState "closing", label:'${name}', action:"garageDoorControl.open", backgroundColor:"#ffffff", icon: "st.doors.garage.garage-closing", nextState:"opening"
			}
        }
        
		standardTile("refresh", "device.door", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        
        standardTile("lock", "device.lockStatus", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "unlock" , label:'', action:"lock", icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-garage.src/lockopen@2x.png", backgroundColor:"#00a0dc"
            state "locked", label:'', action:"lock", icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-garage.src/lock@2x.png", backgroundColor:"#e86d13"
        }
        
        standardTile("override", "device.overrideMode", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "on" , label:'', action:"override", icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-garage.src/override@2x.png", backgroundColor:"#00a0dc"
            state "off", label:'', action:"override", icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-garage.src/override@2x.png", backgroundColor:"#ffffff"
        }        
        
        valueTile("lastevent", "lastevent", width: 2, height: 2) {
    		state "lastevent", label:'Last event\r\n${currentValue}'
		}        
        
        valueTile("ip", "ip", width: 2, height: 2) {
    		state "ip", label:'IP Address\r\n${currentValue}'
		}
        
        valueTile("uptime", "uptime", width: 2, height: 2) {
    		state "uptime", label:'Up time\r\n${currentValue}'
		}         
	}
    
    main(["door"])
	details(["door",
    	"refresh", "override", "lock", "lastevent", "ip", "uptime"])      
}

// handle commands

def installed() {
	log.debug "Executing 'installed'"
    refresh()
}

def configure() {
	log.debug "Executing 'configure'"
	initializeCheckin()
}

def ping() {
	log.debug "Executing 'ping'"
	refresh()
}

def poll() {
	log.debug "Executing 'poll'"
	refresh()
}

def deviceNotification() {
	log.debug "Executing 'deviceNotification'"
	// TODO: handle 'deviceNotification' command
}

def refresh() {
	log.debug "Executing 'refresh'"
    def cmds = []
    cmds << subscribeAction()
    cmds << getAction("/status")
    response(cmds)
}

def sync(ip, port) {
	log.debug "Executing 'sync'"
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

def open() {
	log.debug "Executing 'open'"
    if (device.currentValue("lockStatus") == "locked") { 
    	log.debug "Door is lock, updating state to closed"
        if (parent.sendNotification != null) {
            parent.sendNotification("El garage esta asegurado, quita el seguro para poder abrirlo")
        }
        //Need to send both events to clear the current state
        sendEvent(name: "door", value: "closing")
        sendEvent(name: "door", value: "closed")
    } else {
        def cmds = []
        cmds << getAction("/open")
        response(cmds)
    }
}

def close() {
	log.debug "Executing 'close'"
    //Is not possible this scenario because you can´t lock when door is open, but we included
    if (device.currentValue("lockStatus") == "locked") {
    	log.debug "Door is lock, updating state to open"
        if (parent.sendNotification != null) {
            parent.sendNotification("El garage esta asegurado, quita el seguro para poder cerrarlo")
        }
        //Need to send both events to clear the current state
        sendEvent(name: "door", value: "opening")
        sendEvent(name: "door", value: "open")
    } else {
        def cmds = []
        cmds << getAction("/close")
        response(cmds)
    }
}

def lock() {
	log.debug "Executing 'lock'"
    def cmds = []
    cmds << getAction("/lock")
    response(cmds)
}

def override() {
	log.debug "Executing 'override'"
    def cmds = []
    cmds << getAction("/override")
    response(cmds)
}

// parse events into attributes
def parse(String description) {
	log.debug "Executing 'parse'"
    def events = []

	def msg = parseLanMessage(description)
    def json = msg.json
    log.debug "Lan Message : ${msg}"
    
    if (json != null) {
    	if (json.containsKey("uptime")) {
        	events << createEvent(name: "uptime", value: json.uptime, displayed: false)
    	}
    	if (json.containsKey("lockStatus")) {
        	events << createEvent(name: "lockStatus", value: json.lockStatus, displayed:false)
    	}
        if (json.containsKey("override")) {
        	events << createEvent(name: "overrideMode", value: json.override, displayed:false)
        }        
    	if (json.containsKey("door")) {
            def descriptionText = ""
            def displayed = false
            if (json.containsKey("fromAction") && json.fromAction == "true") {
            	events << createEvent(name: "lastevent", value: convertToLocalTimeString(new Date()), displayed: false)
                //descriptionText = "Garage door is ${json.door}"
                displayed = true
            }
        	events << createEvent(name: "door", value: json.door, displayed: displayed)
    	}
        if (json.containsKey("alarm")) {
        	if (json.alarm == "open_garage_timeout") {
                if (parent.sendNotification != null) {
                    parent.sendNotification("ATENCION! El garage permanece abierto")
                }        		
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

private initializeCheckin() {
    //Time in mins...
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
