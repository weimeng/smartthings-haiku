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

def on() {
    sendEvent(name: "switch", value: "on")
    parent.fanOn()
}

def off() {
    sendEvent(name: "switch", value: "off")
    parent.fanOff()
}

def setLevel(level) {
    sendEvent(name: "level", value: level)
    Integer speed = convertPercentToSpeed(level)
    sendEvent(name: "fanSpeed", value: speed)
    parent.fanSpeed(speed)
}

// helper methods

private Integer convertPercentToSpeed(percent) {
    Integer speed = Math.ceil(percent / 100.0 * 7)

    // Make sure fan speed isn't higher than 7
    speed = (speed > 7) ? 7 : speed

    // Don't let fan speed be set to 0
    speed = (speed < 1) ? 1 : speed

    return speed
}
