/**
 *  Haiku Fan Light Control
 *
 *  Copyright 2018 Wei-Meng Lee
 *
 */

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

def on() {
    sendEvent(name: "switch", value: "on")
    parent.lightOn()
}

def off() {
    sendEvent(name: "switch", value: "off")
    parent.lightOff()
}

def setLevel(level) {
    sendEvent(name: "level", value: level)
    Integer lightLevel = convertPercentToLightLevel(level)
    sendEvent(name: "lightLevel", value: lightLevel)
    parent.lightLevel(lightLevel)
}

// helper methods

private Integer convertPercentToLightLevel(percent) {
    Integer level = Math.ceil(percent / 100.0 * 16)

    // Make sure light level isn't higher than 16
    level = (level > 16) ? 16 : level

    // Don't let light level be set to 0
    level = (level < 1) ? 1 : level

    return level
}