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
        capability "Switch"
        capability "Switch Level"

        command "off"
        command "on"
        command "setLevel"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale :2) {
        standardTile("fan", "device.switch") {
            state "on", icon: "st.Lighting.light24"
            state "off", icon: "st.Lighting.light24"
        }

        standardTile("fanPowerOn", "device.switch", height: 2, width: 3, decoration: "flat") {
            state "on", label: "On", action: "on", icon: "st.Lighting.light24"
            state "off", label: "On", action: "on", icon: "st.Lighting.light24"
        }

        standardTile("fanPowerOff", "device.switch", height: 2, width: 3, decoration: "flat") {
            state "on", label: "Off", action: "off", icon: "st.samsung.da.RC_ic_power"
            state "off", label: "Off", action: "off", icon: "st.samsung.da.RC_ic_power"
        }

		controlTile("fanSpeed", "device.level", "slider", height: 2, width: 6) {
            state "level", action: "setLevel"
        }

        valueTile("lastKnownFanSpeed", "device.fanSpeed", height: 2, width: 6) {
            state "val", label: 'Last known fan speed: ${currentValue}'
        }

        main("fan")

        details(["fanPowerOn", "fanPowerOff", "fanSpeed", "lastKnownFanSpeed"])
	}
}

// parse events into attributes
def parse(String description) {
    log.debug "Parsing '${description}'"
}

// handle commands

def off() {
    sendEvent(name: "switch", value: "off")
    sendRequest("<" + deviceMac + ";FAN;PWR;OFF>")
}

def on() {
    sendEvent(name: "switch", value: "on")
    sendRequest("<" + deviceMac + ";FAN;PWR;ON>")
}

def setLevel(level) {
    sendEvent(name: "level", value: level)
    Integer speed = convertPercentToSpeed(level)
    sendEvent(name: "fanSpeed", value: speed)
    sendRequest("<" + deviceMac + ";FAN;SPD;SET;" + speed + ">")
}

// helper methods

def sendRequest(message) {
    def hosthex = convertIPToHex(deviceIP)
    def porthex = convertPortToHex(31415)
    device.deviceNetworkId = "$hosthex:$porthex"
    def hubAction = new physicalgraph.device.HubAction(message, physicalgraph.device.Protocol.LAN)
    return hubAction
}

private Integer convertPercentToSpeed(percent) {
    Integer speed = Math.ceil(percent / 100.0 * 7)

    // Make sure fan speed isn't higher than 7
    speed = (speed > 7) ? 7 : speed

    // Don't let fan speed be set to 0
    speed = (speed < 1) ? 1 : speed

    return speed
}

private String convertIPToHex(ipAddress) {
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02X', it.toInteger() ) }.join()
    return hex
}

private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04X', port.toInteger() )
    return hexport
}