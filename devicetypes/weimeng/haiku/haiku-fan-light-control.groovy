/**
 *  Haiku Fan Light Control
 *
 *  Copyright 2018 Wei-Meng Lee
 *
 */

preferences {
	input("deviceMac", "text", title: "Device MAC address", description: "The device's MAC address", required: true)
	input("deviceIP", "text", title: "Device IP address", description: "The device's IP address", required: true)
}

metadata {
	definition (name: "Haiku Fan Light Control", namespace: "weimeng/smartthings-haiku", author: "weimeng") {
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
        standardTile("light", "device.switch") {
            state "on", icon: "st.Lighting.light21"
            state "off", icon: "st.Lighting.light21"
        }

        standardTile("lightPowerOn", "device.switch", height: 2, width: 3, decoration: "flat") {
            state "on", label: "On", action: "on", icon: "st.Lighting.light21"
            state "off", label: "On", action: "on", icon: "st.Lighting.light21"
        }

        standardTile("lightPowerOff", "device.switch", height: 2, width: 3, decoration: "flat") {
            state "on", label: "Off", action: "off", icon: "st.samsung.da.RC_ic_power"
            state "off", label: "Off", action: "off", icon: "st.samsung.da.RC_ic_power"
        }

		controlTile("lightLevel", "device.level", "slider", height: 2, width: 6) {
            state "level", action: "setLevel"
        }

        valueTile("lastKnownLightLevel", "device.lightLevel", height: 2, width: 6) {
            state "val", label: 'Last known light level: ${currentValue}'
        }

        main("light")

        details(["lightPowerOn", "lightPowerOff", "lightLevel", "lastKnownLightLevel"])
	}
}

// parse events into attributes
def parse(String description) {
    log.debug "Parsing '${description}'"
}

// handle commands

def off() {
    sendEvent(name: "switch", value: "off")
    sendRequest("<" + deviceMac + ";LIGHT;PWR;OFF>")
}

def on() {
    sendEvent(name: "switch", value: "on")
    sendRequest("<" + deviceMac + ";LIGHT;PWR;ON>")
}

def setLevel(level) {
    sendEvent(name: "level", value: level)
    Integer lightLevel = convertPercentToLightLevel(level)
    sendEvent(name: "lightLevel", value: lightLevel)
    sendRequest("<" + deviceMac + ";LIGHT;LEVEL;SET;" + lightLevel + ">")
}

// helper methods

def sendRequest(message) {
    def hosthex = convertIPToHex(deviceIP)
    def porthex = convertPortToHex(31415)
    device.deviceNetworkId = "${hosthex}:${porthex}-light"
    def hubAction = new physicalgraph.device.HubAction(message, physicalgraph.device.Protocol.LAN, "${hosthex}:${porthex}")
    sendHubCommand(hubAction)
}

private Integer convertPercentToLightLevel(percent) {
    Integer level = Math.ceil(percent / 100.0 * 16)

    // Make sure light level isn't higher than 16
    level = (level > 16) ? 16 : level

    // Don't let light level be set to 0
    level = (level < 1) ? 1 : level

    return level
}

private String convertIPToHex(ipAddress) {
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02X', it.toInteger() ) }.join()
    return hex
}

private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04X', port.toInteger() )
    return hexport
}