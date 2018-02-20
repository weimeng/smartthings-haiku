/**
 *  Haiku Fan Speed Control
 *
 *  Copyright 2018 Wei-Meng Lee
 *
 */

preferences {
	input("deviceMac", "text", title: "Device MAC address", description: "The device's MAC address", required: true)
	input("deviceIP", "text", title: "Device IP address", description: "The device's IP address", required: true)
}

metadata {
	definition (name: "Haiku Fan Speed Control", namespace: "weimeng/smartthings-haiku", author: "weimeng") {
        capability "Fan Speed"
        capability "Switch"

        command "off"
        command "on"
        command "setFanSpeed"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale :2) {
        standardTile("fan", "device.fanSpeed") {
            state "0", label: "Off", action: "on",  icon: "st.Lighting.light24"
            state "1", label: 'Speed ${currentValue}', action: "off", icon:"st.Lighting.light24", backgroundColor: "#00a0dc"
            state "2", label: 'Speed ${currentValue}', action: "off", icon:"st.Lighting.light24", backgroundColor: "#00a0dc"
            state "3", label: 'Speed ${currentValue}', action: "off", icon:"st.Lighting.light24", backgroundColor: "#00a0dc"
            state "4", label: 'Speed ${currentValue}', action: "off", icon:"st.Lighting.light24", backgroundColor: "#00a0dc"
            state "5", label: 'Speed ${currentValue}', action: "off", icon:"st.Lighting.light24", backgroundColor: "#00a0dc"
            state "6", label: 'Speed ${currentValue}', action: "off", icon:"st.Lighting.light24", backgroundColor: "#00a0dc"
            state "7", label: 'Speed ${currentValue}', action: "off", icon:"st.Lighting.light24", backgroundColor: "#00a0dc"
        }

        multiAttributeTile(name: "fanPower", type: "generic", width: 2, height: 2) {
            tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", action: "off", label: 'On', icon:"st.Lighting.light24", backgroundColor: "#00a0dc"
                attributeState "off", action: "on", label: 'Off', icon:"st.Lighting.light24", backgroundColor: "#ffffff"
            }
        }

		controlTile("fanSpeed", "device.fanSpeed", "slider", height: 2, width: 6, range: "(1..7)") {
            state "fanSpeed", action: "setFanSpeed"
        }

        main("fan")

        details(["fanPower", "fanSpeed"])
	}
}

// parse events into attributes
def parse(String description) {
    log.debug "Parsing '${description}'"
}

// handle commands

def off() {
    sendEvent(name: "switch", value: "off")
    sendEvent(name: "fanSpeed", value: 0)
    sendRequest("<" + deviceMac + ";FAN;PWR;OFF>")
}

def on() {
    sendEvent(name: "switch", value: "on")
    sendRequest("<" + deviceMac + ";FAN;PWR;ON>")
}

def setFanSpeed(speed) {
    sendEvent(name: "fanSpeed", value: speed)
    sendRequest("<" + deviceMac + ";FAN;SPD;SET;" + speed + ">")
}

// helper methods

def sendRequest(message) {
    def hosthex = convertIPtoHex(deviceIP)
    def porthex = convertPortToHex(31415)
    device.deviceNetworkId = "$hosthex:$porthex"
    def cmds = []
    def hubAction = new physicalgraph.device.HubAction(message, physicalgraph.device.Protocol.LAN)
    return hubAction
}

private String convertIPtoHex(ipAddress) {
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02X', it.toInteger() ) }.join()
    return hex
}

private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04X', port.toInteger() )
    return hexport
}