/**
 *  SmartBit Metrics
 *
 *  Copyright 2020 JuvsGamer
 *  Version: 0.0.1
 *  Date: 28 APR 2020
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
	definition (name: "SmartBit Metrics", namespace: "bits", author: "JuvsGamer") {
        capability "Sensor"
        capability "Refresh"
		capability "Configuration"
		capability "Health Check"
		capability "Polling"

        command "action"
        command "reboot"
        command "brodcast"
        
        // attribute "lockStatus", "string"
        // attribute "overrideMode", "string"
	}

    preferences {
    } 

	simulator {
		// TODO: define status and reply messages here
	}
    
	tiles (scale: 2){      

        standardTile("tank1Level_icon", "tank1Level_icon", inactiveLabel: false, decoration: "flat", width: 3, height: 2) {
            state "100", label:'${currentValue}%', icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/tank-100@2x.png", backgroundColor:"#00a0dc"
            state "90" , label:'${currentValue}%', icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/tank-90@2x.png", backgroundColor:"#00a0dc"
            state "80" , label:'${currentValue}%', icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/tank-80@2x.png", backgroundColor:"#00a0dc"
            state "70" , label:'${currentValue}%', icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/tank-70@2x.png", backgroundColor:"#00a0dc"
            state "60" , label:'${currentValue}%', icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/tank-60@2x.png", backgroundColor:"#00a0dc"
            state "50" , label:'${currentValue}%', icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/tank-50@2x.png", backgroundColor:"#00a0dc"
            state "40" , label:'${currentValue}%', icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/tank-40@2x.png", backgroundColor:"#00a0dc"
            state "30" , label:'${currentValue}%', icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/tank-30@2x.png", backgroundColor:"#00a0dc"
            state "20" , label:'${currentValue}%', icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/tank-20@2x.png", backgroundColor:"#00a0dc"
            state "10" , label:'${currentValue}%', icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/tank-10@2x.png", backgroundColor:"#00a0dc"
        }
        
        standardTile("tank2Level_icon", "tank2Level_icon", inactiveLabel: false, decoration: "flat", width: 3, height: 2) {
            state "100", label:'${currentValue}%', icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/tank-100@2x.png", backgroundColor:"#e86d13"
            state "90" , label:'${currentValue}%', icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/tank-90@2x.png", backgroundColor:"#e86d13"
            state "80" , label:'${currentValue}%', icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/tank-80@2x.png", backgroundColor:"#e86d13"
            state "70" , label:'${currentValue}%', icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/tank-70@2x.png", backgroundColor:"#e86d13"
            state "60" , label:'${currentValue}%', icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/tank-60@2x.png", backgroundColor:"#e86d13"
            state "50" , label:'${currentValue}%', icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/tank-50@2x.png", backgroundColor:"#e86d13"
            state "40" , label:'${currentValue}%', icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/tank-40@2x.png", backgroundColor:"#e86d13"
            state "30" , label:'${currentValue}%', icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/tank-30@2x.png", backgroundColor:"#e86d13"
            state "20" , label:'${currentValue}%', icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/tank-20@2x.png", backgroundColor:"#e86d13"
            state "10" , label:'${currentValue}%', icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/tank-10@2x.png", backgroundColor:"#e86d13"
            
        }        

        valueTile("tank1Level", "tank1Level", width: 3, height: 1) {
    		state "tank1Level", label:'Tank 1 is \r\n${currentValue}%'
		}
        
        valueTile("tank2Level", "tank2Level", width: 3, height: 1) {
    		state "tank2Level", label:'Tank 2 is \r\n${currentValue}%'
		}        
        
		standardTile("refresh", "refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:"Refresh", action:"refresh", icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/refresh@2x.png", backgroundColor:"#FFFFFF"
		}
               
        standardTile("reboot", "reboot", decoration: "flat", height: 2, width: 2, inactiveLabel: false) {
            state "default", label:"Restart", action:"reboot", icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/reboot@2x.png", backgroundColor:"#bf2121"
        }
        
        standardTile("brodcast", "brodcast", decoration: "flat", height: 2, width: 2, inactiveLabel: false) {
            state "default", label:"Brodcast", action:"brodcast", icon:"https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/devicetypes/bits/smartbit-metrics.src/broadcast@2x.png", backgroundColor:"#FFFFFF"
        }        
        
        valueTile("ip", "ip", width: 2, height: 1) {
    		state "ip", label:'IP Address\r\n${currentValue}'
		}
        
        valueTile("uptime", "uptime", width: 2, height: 1) {
    		state "uptime", label:'Up time\r\n${currentValue}'
		}
        
        valueTile("version", "version", width: 2, height: 1) {
    		state "version", label:'Version\r\n${currentValue}'
		}        
	}
    
    main(["tank1Level_icon", "tank2Level_icon"])
	details(["tank1Level_icon", "tank2Level_icon", "tank1Level", "tank2Level", "refresh", "reboot", "brodcast", "ip", "uptime", "version"])      
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

def reboot() {
	log.debug "Executing 'reboot'"
    def cmds = []
    cmds << getAction("/reboot")
    response(cmds)
}

def brodcast() {
	log.debug "Executing 'brodcast'"
    def cmds = []
    cmds << getAction("/info")
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
    	if (json.containsKey("tank1Level")) {
        	events << createEvent(name: "tank1Level", value: json.tank1Level, displayed:false)
            setTankLevelIcon("tank1Level", json.tank1Level)
    	}
    	if (json.containsKey("tank2Level")) {
        	events << createEvent(name: "tank2Level", value: json.tank2Level, displayed:false)
            setTankLevelIcon("tank2Level", json.tank2Level)
    	}    
    	if (json.containsKey("uptime")) {
        	events << createEvent(name: "uptime", value: json.uptime, displayed: false)
    	}
    	if (json.containsKey("version")) {
        	events << createEvent(name: "version", value: json.version, displayed:false)
    	}                
        if (json.containsKey("state")) {
        	if (json.state == "") {
            	
            }
        	events << createEvent(name: "led", value: json.led, displayed:false)
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

private setTankLevelIcon(variable, level) {
	def standarLevel = 0;
    if (level >= 0 && level <= 10) {
    	standarLevel = 10;
    } else if (level >= 11 && level <= 20) {
    	standarLevel = 20;
    } else if (level >= 21 && level <= 30) {
    	standarLevel = 30;
    } else if (level >= 31 && level <= 40) {
    	standarLevel = 40;
    } else if (level >= 41 && level <= 50) {
    	standarLevel = 50;
    } else if (level >= 51 && level <= 60) {
    	standarLevel = 60;
    } else if (level >= 61 && level <= 70) {
    	standarLevel = 70;
    } else if (level >= 71 && level <= 80) {
    	standarLevel = 80;
    } else if (level >= 81 && level <= 90) {
    	standarLevel = 90;
    } else if (level >= 91 && level <= 100) {
    	standarLevel = 100;
    } 
	sendEvent(name: "${variable}_icon", value: standarLevel)
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