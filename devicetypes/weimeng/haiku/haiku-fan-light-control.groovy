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
	}
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