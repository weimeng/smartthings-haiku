metadata {
    definition(name: "Haiku Fan", namespace: "weimeng/smartthings-haiku", author: "weimeng") {
        capability "Switch"
        capability "Switch Level"
    }

    tiles(scale: 2) {
        standardTile("haikuFan", "device.switch") {
            state "on", icon: "st.Lighting.light24"
            state "off", icon: "st.Lighting.light24"
        }

        main("haikuFan")
    }

    preferences {
        input name: "deviceMac", type: "text", title: "MAC address", description: "The device's MAC address", required: true
        input name: "deviceIp", type: "text", title: "IP address", description: "The device's IP address", required: true
    }
}

def installed() {
    log.debug 'Installing...'
    device.deviceNetworkId = getDeviceNetworkId()
    createChildDevices()
}

def updated() {
    device.deviceNetworkId = getDeviceNetworkId()
    def childDevices = getChildDevices()

    if (childDevices.size() == 0) {
        createChildDevices()
    }
}

// Fan

def fanOn() {
    sendCommand("<" + deviceMac + ";FAN;PWR;ON>")
}

def fanOff() {
    sendCommand("<" + deviceMac + ";FAN;PWR;OFF>")
}

def fanSpeed(speed) {
    log.debug "Trying to set fan speed to ${speed}"
    sendCommand("<" + deviceMac + ";FAN;SPD;SET;" + speed + ">")
}

// Light

def lightOn() {
    sendCommand("<" + deviceMac + ";LIGHT;PWR;ON>")
}

def lightOff() {
    sendCommand("<" + deviceMac + ";LIGHT;PWR;OFF>")
}

def lightLevel(level) {
    log.debug "Trying to set light level to ${level}"
    sendCommand("<" + deviceMac + ";LIGHT;LEVEL;SET;" + level + ">")
}

private sendCommand(message) {
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