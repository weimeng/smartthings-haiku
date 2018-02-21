metadata {
    definition(name: "Haiku Fan", namespace: "weimeng/smartthings-haiku", author: "weimeng") {
        capability "Switch"
        capability "Switch Level"

        attribute "fanSpeed", "number"
        attribute "fanSwitch", "enum", ["on", "off"]

        attribute "lightLevel", "number"
        attribute "lightSwitch", "enum", ["on", "off"]

        command "fanOn"
        command "fanOff"
        command "fanSpeed"

        command "lightOn"
        command "lightOff"
        command "lightLevel"
    }

    tiles(scale: 2) {
        standardTile("haikuFan", "device.switch") {
            state "on", icon: "st.Lighting.light24"
            state "off", icon: "st.Lighting.light24"
        }

        multiAttributeTile(name: "vanity", type: "generic", width: 6, height: 4, canChangeIcon: true) {
            tileAttribute("device.fanSwitch", key: "PRIMARY_CONTROL") {
                attributeState "on", label: "", icon: "st.Lighting.light24", backgroundColor: "#00a0dc"
                attributeState "off", label: "", icon: "st.Lighting.light24", backgroundColor: "#00a0dc"
            }

            tileAttribute ("device.lightSwitch", key: "SECONDARY_CONTROL") {
               attributeState "on", label: "Controls are not aware of fan & light state", icon: "st.thermostat.thermostat-right"
               attributeState "off", label: "Controls are not aware of fan & light state", icon: "st.thermostat.thermostat-right"
            }
        }

        standardTile("fanPowerOn", "device.fanSwitch", height: 2, width: 2, decoration: "flat") {
            state "on", label: "On", action: "fanOn", icon: "st.Lighting.light24", backgroundColor: "#00a0dc"
            state "off", label: "On", action: "fanOn", icon: "st.Lighting.light24", backgroundColor: "#ffffff"
        }

        standardTile("fanPowerOff", "device.fanSwitch", height: 2, width: 2, decoration: "flat") {
            state "on", label: "Off", action: "fanOff", icon: "st.samsung.da.RC_ic_power", backgroundColor: "#ffffff"
            state "off", label: "Off", action: "fanOff", icon: "st.samsung.da.RC_ic_power", backgroundColor: "#00a0dc"
        }

		controlTile("fanSpeed", "device.fanSpeed", "slider", height: 2, width: 2, range: (1..7)) {
            state "fanSpeed", action: "fanSpeed"
        }

        standardTile("lightPowerOn", "device.lightSwitch", height: 2, width: 2, decoration: "flat") {
            state "on", label: "On", action: "lightOn", icon: "st.Lighting.light21", backgroundColor: "#00a0dc"
            state "off", label: "On", action: "lightOn", icon: "st.Lighting.light21", backgroundColor: "#ffffff"
        }

        standardTile("lightPowerOff", "device.lightSwitch", height: 2, width: 2, decoration: "flat") {
            state "on", label: "Off", action: "lightOff", icon: "st.samsung.da.RC_ic_power", backgroundColor: "#ffffff"
            state "off", label: "Off", action: "lightOff", icon: "st.samsung.da.RC_ic_power", backgroundColor: "#00a0dc"
        }

        controlTile("lightLevel", "device.lightLevel", "slider", height: 2, width: 2, range: (1..16)) {
            state "lightLevel", action: "lightLevel"
        }

        main("haikuFan")

        details(["vanity", "fanPowerOn", "fanSpeed", "fanPowerOff", "lightPowerOn", "lightLevel", "lightPowerOff"])
    }

    preferences {
        input name: "deviceMac", type: "text", title: "MAC address", description: "The device's MAC address", required: true
        input name: "deviceIp", type: "text", title: "IP address", description: "The device's IP address", required: true
    }
}

def installed() {
    createChildDevices()
}

def updated() {
    // Create child devices if not yet created
    def childDevices = getChildDevices()
    if (childDevices.size() == 0) {
        createChildDevices()
    }
}

// Fan

def fanOn() {
    log.debug "Turning fan on"
    sendCommand("<" + deviceMac + ";FAN;PWR;ON>")
    sendEvent(name: "fanSwitch", value: "on")
}

def fanOff() {
    log.debug "Turning fan off"
    sendCommand("<" + deviceMac + ";FAN;PWR;OFF>")
    sendEvent(name: "fanSwitch", value: "off")
}

def fanSpeed(Integer speed) {
    sendCommand("<" + deviceMac + ";FAN;SPD;SET;" + speed + ">")
    sendEvent(name: "fanSpeed", value: speed)
}

// Light

def lightOn() {
    log.debug "Turning light on"
    sendCommand("<" + deviceMac + ";LIGHT;PWR;ON>")
    sendEvent(name: "lightSwitch", value: "on")
}

def lightOff() {
    log.debug "Turning light off"
    sendCommand("<" + deviceMac + ";LIGHT;PWR;OFF>")
    sendEvent(name: "lightSwitch", value: "off")
}

def lightLevel(Integer level) {
    sendCommand("<" + deviceMac + ";LIGHT;LEVEL;SET;" + level + ">")
    sendEvent(name: "lightLevel", value: level)
}

// Helper methods

private sendCommand(String message) {
    device.deviceNetworkId = getDeviceNetworkId()
    def hubAction = new physicalgraph.device.HubAction(message, physicalgraph.device.Protocol.LAN)
    sendHubCommand(hubAction)
}

private void createChildDevices() {
    log.debug 'Creating child devices'
    addChildDevice("Haiku Fan Speed Control", "${getDeviceNetworkId()}-fan", null, [completedSetup: true, label: "${device.displayName} Speed", isComponent: false, componentName: "fan", componentLabel: "Fan"])
    addChildDevice("Haiku Fan Light Control", "${getDeviceNetworkId()}-light", null, [completedSetup: true, label: "${device.displayName} Light", isComponent: false, componentName: "light", componentLabel: "Light"])
}

private String getDeviceNetworkId() {
    def ipHex = deviceIp.tokenize( '.' ).collect { String.format('%02X', it.toInteger()) }.join()
    def port = 31415
    def portHex = port.toString().format('%04X', port.toInteger())
    return "${ipHex}:${portHex}"
}