/**
 *  SmartBits (Connect)
 *
 *  Copyright 2018 Juvenal Guzman
 *  Version: 1.0.2
 *  Date: 19 MAR 2018 
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
definition(
    name: "SmartBits (Connect)",
    namespace: "bits",
    author: "JuvsGamer",
    description: "Service Manager for SmartBits components",
    category: "Convenience",
    iconUrl: "https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/smartapps/bits/smartbits-connect.src/smartbits-logo.png",
    iconX2Url: "https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/smartapps/bits/smartbits-connect.src/smartbits-logo@x2.png",
    iconX3Url: "https://raw.githubusercontent.com/juvs/SmartThingsPublic/master/smartapps/bits/smartbits-connect.src/smartbits-logo@x3.png")


preferences {
	page(name: "mainPage")
    page(name: "configurePDevice")
    page(name: "deletePDevice")
    page(name: "changeName")
    page(name: "discoveryPage", title: "Device Discovery", content: "discoveryPage", refreshTimeout:5)
    page(name: "addDevices", title: "Add SmartBits Devices", content: "addDevices")
    page(name: "manuallyAdd")
    page(name: "manuallyAddConfirm")
    page(name: "deviceDiscovery")
}

def mainPage() {
	dynamicPage(name: "mainPage", title: "Manage your SmartBit device", nextPage: null, uninstall: true, install: true) {
        section("Configure"){
           href "deviceDiscovery", title:"Discover Devices", description:""
           href "manuallyAdd", title:"Manually Add Device", description:""
        }
        section("Installed Devices"){
            getChildDevices().sort({ a, b -> a["deviceNetworkId"] <=> b["deviceNetworkId"] }).each {
                href "configurePDevice", title:"$it.label", description:"", params: [did: it.deviceNetworkId]
            }
        }
    }
}

def configurePDevice(params){
    if (params?.did || params?.params?.did) {
        if (params.did) {
            state.currentDeviceId = params.did
            state.currentDisplayName = getChildDevice(params.did)?.displayName
        } else {
            state.currentDeviceId = params.params.did
            state.currentDisplayName = getChildDevice(params.params.did)?.displayName
        }
    }
    def typeName = getChildDevice(state.currentDeviceId).getTypeName()
    log.debug "configurePDevice()"
    log.debug "typeName = $typeName"
    
    if (getChildDevice(state.currentDeviceId) != null) {
        getChildDevice(state.currentDeviceId).configure()
    }
    
    if (typeName == "SmartBit Bell") {
        dynamicPage(name: "configurePDevice", title: "Configure your SmartBit Bell device", nextPage: null) {
            section {
                app.updateSetting("${state.currentDeviceId}_label", getChildDevice(state.currentDeviceId).label)
                input "${state.currentDeviceId}_label", "text", title:"Device Name", description: "", required: false
                href "changeName", title:"Change Device Name", description: "Edit the name above and click here to change it"
            }
            section ("Turn on other devices for some amount of time") {
                input "${state.currentDeviceId}_otherDevices", "capability.switch", multiple: true, title: "Which?"
                input "${state.currentDeviceId}_turnOnSecs", "number", title: "How many seconds?", defaultValue: 4
            }
            section {
                href "deletePDevice", title:"Delete $state.currentDisplayName", description: ""
            }
        }
    } else if (typeName == "SmartBit Garage") {
        dynamicPage(name: "configurePDevice", title: "Configure your SmartBit Garage device", nextPage: null) {
            section {
                app.updateSetting("${state.currentDeviceId}_label", getChildDevice(state.currentDeviceId).label)
                input "${state.currentDeviceId}_label", "text", title:"Device Name", description: "", required: false
                href "changeName", title:"Change Device Name", description: "Edit the name above and click here to change it"
            }
            section {
                href "deletePDevice", title:"Delete $state.currentDisplayName", description: ""
            }
        }    
    }
}

def manuallyAdd(){
   dynamicPage(name: "manuallyAdd", title: "Manually add a SmartBit device", nextPage: "manuallyAddConfirm") {
		section {
			paragraph "This process will manually create a SmartBit device based on the entered IP address. The SmartApp needs to then communicate with the device to obtain additional information from it. Make sure the device is on and connected to your wifi network."
            input "deviceType", "enum", title:"Device Type", description: "", required: false, options: ["SmartBit Garage", "SmartBit Bell"]
            input "ipAddress", "text", title:"IP Address", description: "", required: false 
		}
    }
}

def manuallyAddConfirm(){
   if ( ipAddress =~ /^(?:[0-9]{1,3}\.){3}[0-9]{1,3}$/) {
       log.debug "Creating SmartBit device with dni: ${convertIPtoHex(ipAddress)}:${convertPortToHex("80")}"
       addChildDevice("juvs", deviceType ? deviceType : "SmartBit Garage", "${convertIPtoHex(ipAddress)}:${convertPortToHex("80")}", location.hubs[0].id, [
           "label": (deviceType ? deviceType : "SmartBit Garage") + " (${ipAddress})",
           "data": [
           		"ip": ipAddress,
           		"port": "80" 
           ]
       ])
   
       app.updateSetting("ipAddress", "")
            
       dynamicPage(name: "manuallyAddConfirm", title: "Manually add a SmartBit device", nextPage: "mainPage") {
		   section {
			   paragraph "The device has been added. Press next to return to the main page."
	    	}
       }
    } else {
        dynamicPage(name: "manuallyAddConfirm", title: "Manually add a SmartBit device", nextPage: "mainPage") {
		    section {
			    paragraph "The entered ip address is not valid. Please try again."
		    }
        }
    }
}

def deletePDevice(){
    try {
        unsubscribe()
        deleteChildDevice(state.currentDeviceId)
        dynamicPage(name: "deletePDevice", title: "Deletion Summary", nextPage: "mainPage") {
            section {
                paragraph "The device has been deleted. Press next to continue"
            } 
        }
    
	} catch (e) {
        dynamicPage(name: "deletePDevice", title: "Deletion Summary", nextPage: "mainPage") {
            section {
                paragraph "Error: ${(e as String).split(":")[1]}."
            } 
        }
    
    }
}

def changeName(){
    def thisDevice = getChildDevice(state.currentDeviceId)
    thisDevice.label = settings["${state.currentDeviceId}_label"]

    dynamicPage(name: "changeName", title: "Change Name Summary", nextPage: "mainPage") {
	    section {
            paragraph "The device has been renamed. Press \"Next\" to continue"
        }
    }
}

def discoveryPage(){
	return deviceDiscovery()
}

def deviceDiscovery(params=[:])
{
	def devices = devicesDiscovered()
    
	int deviceRefreshCount = !state.deviceRefreshCount ? 0 : state.deviceRefreshCount as int
	state.deviceRefreshCount = deviceRefreshCount + 1
	def refreshInterval = 3
    
	def options = devices ?: []
	def numFound = options.size() ?: 0

	if ((numFound == 0 && state.deviceRefreshCount > 25) || params.reset == "true") {
    	log.trace "Cleaning old device memory"
    	state.devices = [:]
        state.deviceRefreshCount = 0
        app.updateSetting("selectedDevice", "")
    }

	ssdpSubscribe()

	//SmartBit devices discovery request every 15 //25 seconds
	if((deviceRefreshCount % 5) == 0) {
		discoverDevices()
	}

	//description.xml request every 3 seconds except on discoveries
	if(((deviceRefreshCount % 3) == 0) && ((deviceRefreshCount % 5) != 0)) {
		verifyDevices()
	}

	return dynamicPage(name:"deviceDiscovery", title:"Discovery Started!", nextPage:"addDevices", refreshInterval:refreshInterval, uninstall: true) {
		section("Please wait while we discover your SmartBit devices. Discovery can take five minutes or more, so sit back and relax! Select your device below once discovered.") {
			input "selectedDevices", "enum", required:false, title:"Select SmartBit device (${numFound} found)", multiple:true, options:options
		}
        section("Options") {
			href "deviceDiscovery", title:"Reset list of discovered devices", description:"", params: ["reset": "true"]
		}
	}
}

Map devicesDiscovered() {
	def vdevices = getVerifiedDevices()
	def map = [:]
	vdevices.each {
		def value = "${it.value.name}"
		def key = "${it.value.mac}"
		map["${key}"] = value
	}
	map
}

def getVerifiedDevices() {
	getDevices().findAll{ it?.value?.verified == true }
}

private discoverDevices() {
	log.debug "discoverDevices()"
	sendHubCommand(new physicalgraph.device.HubAction("lan discovery urn:schemas-upnp-org:device:SmartBit:1", physicalgraph.device.Protocol.LAN))
}

def configured() {
	
}

def buttonConfigured(idx) {
	return settings["lights_$idx"]
}

def isConfigured(){
   if(getChildDevices().size() > 0) return true else return false
}

def isVirtualConfigured(did){ 
    def foundDevice = false
    getChildDevices().each {
       if(it.deviceNetworkId != null){
       if(it.deviceNetworkId.startsWith("${did}/")) foundDevice = true
       }
    }
    return foundDevice
}

private virtualCreated(number) {
    if (getChildDevice(getDeviceID(number))) {
        return true
    } else {
        return false
    }
}

private getDeviceID(number) {
    return "${state.currentDeviceId}/${app.id}/${number}"
}

def installed() {
	initialize()
}

def updated() {
	unsubscribe()
    unschedule()
	initialize()
}

def initialize() {
    ssdpSubscribe()
    runEvery5Minutes("ssdpDiscover")
    //Listen to change modes
    subscribe(location, modeChangeHandler)
    subscribe(location, "alarmSystemStatus", onSHMEvent)
}

void ssdpSubscribe() {
	//log.debug "ssdpSubscribe()"
    subscribe(location, "ssdpTerm.urn:schemas-upnp-org:device:SmartBit:1", ssdpHandler)
}

void ssdpDiscover() {
	//log.debug "ssdpDiscover()"
    sendHubCommand(new physicalgraph.device.HubAction("lan discovery urn:schemas-upnp-org:device:SmartBit:1", physicalgraph.device.Protocol.LAN))
}

def ssdpHandler(evt) {
	log.debug "ssdpHandler()"
    def description = evt.description
    def hub = evt?.hubId
    def parsedEvent = parseLanMessage(description)
    parsedEvent << ["hub":hub]

    def devices = getDevices()
    
    String ssdpUSN = parsedEvent.ssdpUSN.toString()
    
    if (devices."${ssdpUSN}") {
        def d = devices."${ssdpUSN}"
        def child = getChildDevice(parsedEvent.mac)
        if (child) {
            def childIP = child.getDeviceDataByName("ip")
            def childPort = child.getDeviceDataByName("port").toString()
            log.debug "Device data: ($childIP:$childPort) - reporting data: (${convertHexToIP(parsedEvent.networkAddress)}:${convertHexToInt(parsedEvent.deviceAddress)})."
            log.debug "Event: ${parsedEvent}"
            if(childIP != convertHexToIP(parsedEvent.networkAddress) || childPort != convertHexToInt(parsedEvent.deviceAddress).toString()){
               log.debug "Device data (${child.getDeviceDataByName("ip")}) does not match what it is reporting(${convertHexToIP(parsedEvent.networkAddress)}). Attempting to update."
               child.sync(convertHexToIP(parsedEvent.networkAddress), convertHexToInt(parsedEvent.deviceAddress).toString())
            }
        }

        if (d.networkAddress != parsedEvent.networkAddress || d.deviceAddress != parsedEvent.deviceAddress) {
            d.networkAddress = parsedEvent.networkAddress
            d.deviceAddress = parsedEvent.deviceAddress
        }
    } else {
        devices << ["${ssdpUSN}": parsedEvent]
    }
}

void verifyDevices() {
    def devices = getDevices().findAll { it?.value?.verified != true }
    devices.each {
        def ip = convertHexToIP(it.value.networkAddress)
        def port = convertHexToInt(it.value.deviceAddress)
        String host = "${ip}:${port}"
        sendHubCommand(new physicalgraph.device.HubAction("""GET ${it.value.ssdpPath} HTTP/1.1\r\nHOST: $host\r\n\r\n""", physicalgraph.device.Protocol.LAN, host, [callback: deviceDescriptionHandler]))
    }
}

def getDevices() {
    state.devices = state.devices ?: [:]
}

void deviceDescriptionHandler(physicalgraph.device.HubResponse hubResponse) {
	log.trace "description.xml response (application/xml)"
	def body = hubResponse.xml
    def friendlyName = body?.device?.friendlyName?.text()
    log.debug "friendlyName device $friendlyName"
	if (body?.device?.modelName?.text().startsWith("SmartBit")) {
		def devices = getDevices()
		def device = devices.find {it?.key?.contains(body?.device?.UDN?.text())}
		if (device) {
			device.value << [name:body?.device?.friendlyName?.text() + " (" + convertHexToIP(hubResponse.ip) + ")", serialNumber:body?.device?.serialNumber?.text(), verified: true]
		} else {
			log.error "/description.xml returned a device that didn't exist"
		}
	}
}

def addDevices() {
    def devices = getDevices()
    def sectionText = ""

    selectedDevices.each { dni ->bridgeLinking
        def selectedDevice = devices.find { it.value.mac == dni }
        def d
        if (selectedDevice) {
            d = getChildDevices()?.find {
                it.deviceNetworkId == selectedDevice.value.mac
            }
        }

        if (!d) {
        	def deviceHandlerName = selectedDevice?.value?.name
            log.debug selectedDevice
            log.debug "Creating ${deviceHandlerName} device with dni: ${selectedDevice.value.mac}, ssdpPath: ${selectedDevice.value.ssdpPath}, ssdpUSN: ${dni}"
            
            if (selectedDevice?.value?.name?.startsWith("SmartBit Garage"))
                deviceHandlerName = "SmartBit Garage"
            //else 
            //    deviceHandlerName = "SmartBit Bell"
                
            def newDevice = addChildDevice("bits", deviceHandlerName, selectedDevice.value.mac, selectedDevice?.value.hub, [
                "label": selectedDevice?.value?.name,
                "data": [
                    "mac": selectedDevice.value.mac,
                    "ip": convertHexToIP(selectedDevice.value.networkAddress),
                    "port": "" + Integer.parseInt(selectedDevice.value.deviceAddress,16),
                    "ssdpUSN": dni,
                    "ssdpPath": selectedDevice.value.ssdpPath,
                    "bitType" : deviceHandlerName
                ]
            ])
            sectionText = sectionText + "Succesfully added ${deviceHandlerName} device with ip address ${convertHexToIP(selectedDevice.value.networkAddress)} \r\n"
        }
        
	} 
    log.debug sectionText
    return dynamicPage(name:"addDevices", title:"Devices Added", nextPage:"mainPage",  uninstall: true) {
        if(sectionText != ""){
            section("Add SmartBit Item Results:") {
                paragraph sectionText
            }
    	} else {
            section("No devices added") {
                paragraph "All selected devices have previously been added"
            }
    	}
	}
}

def uninstalled() {
    unsubscribe()
    getChildDevices().each {
        deleteChildDevice(it.deviceNetworkId)
    }
}

def modeChangeHandler(evt) {
    log.debug "Mode changed to ${evt.value}, not implemented!"
    def currMode = location.mode
}

def onSHMEvent(evt){
	log.debug "onSHMEvent event value is ${evt.value}"
    if (evt.value == "stay" || evt.value == "away") {
        getChildDevices().each {
        	def type = it.getDataValue("bitType");
            //log.debug "${it.deviceNetworkId} - ${type}"
            if (type.startsWith("SmartBit Garage")) {
            	log.debug "Locking SmartBit Garage Door ${it.deviceNetworkId}"
                it.lock();
            }
        }    	
    }
    if (evt.value == "off") {
        getChildDevices().each {
        	def type = it.getDataValue("bitType");
        	//log.debug "${it.deviceNetworkId} - ${type}"            
            if (type.startsWith("SmartBit Garage")) {
            	log.debug "Unlocking SmartBit Garage Door ${it.deviceNetworkId}"
                it.unlock();
            }
        }     
    }
}

void sendNotification(String msg) {
	log.debug "Sending push notificaction..."
	sendPush(msg)
}

//Turn on other devices when is ringing (SmartBit Bell)
void turnOnDevicesWhenRinging(String deviceNetworkId) {
	//log.debug "turnOnDevicesWhenRinging()"
	log.debug "Checking for activate other devices for ${deviceNetworkId}..."
    //def device = app.getChildDevice(deviceNetworkId)
    //log.debug "Device to check $device"
    def otherDevices = settings["${deviceNetworkId}_otherDevices"]
    def turnOnSecs = settings["${deviceNetworkId}_turnOnSecs"]
    log.debug "Turn on $otherDevices, $turnOnSecs secs, device $deviceNetworkId"
    //If other devices (switches) are configured
    if (otherDevices.size() > 0) {
        log.debug "Will send the on() command to ${otherDevices.size()} other devices"
        otherDevices.each {
            it.on()
            it.off([delay: turnOnSecs * 1000])
        }
    }
}

private String convertHexToIP(hex) {
	[convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}

private String convertIPtoHex(ipAddress) { 
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
    return hex
}

private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04x', port.toInteger() )
    return hexport
}
