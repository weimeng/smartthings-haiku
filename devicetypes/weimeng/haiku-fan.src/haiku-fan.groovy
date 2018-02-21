/**
 * Haiku Fan
 *
 * Copyright 2018 Wei-Meng Lee (https://github.com/weimeng)
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
 *  Inspired by code from "system48": https://github.com/System48/Smartthings
 *
 */

metadata {
    definition(name: "Haiku Fan", namespace: "weimeng", author: "Wei-Meng Lee") {
        capability "Switch"
        capability "Switch Level"

        attribute "fanSpeed", "number"
        attribute "lightLevel", "number"
        attribute "lightSwitch", "enum", ["on", "off"]

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
            tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label: "", icon: "st.Lighting.light24", backgroundColor: "#00a0dc"
                attributeState "off", label: "", icon: "st.Lighting.light24", backgroundColor: "#00a0dc"
            }

            tileAttribute ("device.lightSwitch", key: "SECONDARY_CONTROL") {
               attributeState "on", label: "Controls are not aware of fan & light state", icon: "st.thermostat.thermostat-right"
               attributeState "off", label: "Controls are not aware of fan & light state", icon: "st.thermostat.thermostat-right"
            }
        }

        standardTile("fanPowerOn", "device.switch", height: 2, width: 2, decoration: "flat") {
            state "on", label: "On", action: "on", icon: "st.Lighting.light24", backgroundColor: "#00a0dc"
            state "off", label: "On", action: "on", icon: "st.Lighting.light24", backgroundColor: "#ffffff"
        }

        standardTile("fanPowerOff", "device.switch", height: 2, width: 2, decoration: "flat") {
            state "on", label: "Off", action: "off", icon: "st.samsung.da.RC_ic_power", backgroundColor: "#ffffff"
            state "off", label: "Off", action: "off", icon: "st.samsung.da.RC_ic_power", backgroundColor: "#00a0dc"
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
    sendCommand("<" + deviceMac + ";FAN;PWR;ON>")
    sendEvent(name: "fanSwitch", value: "on")
}

def fanOff() {
    sendCommand("<" + deviceMac + ";FAN;PWR;OFF>")
    sendEvent(name: "fanSwitch", value: "off")
}

def fanSpeed(Integer speed) {
    sendCommand("<" + deviceMac + ";FAN;SPD;SET;" + speed + ">")
    sendEvent(name: "fanSpeed", value: speed)
}

// Light

def lightOn() {
    sendCommand("<" + deviceMac + ";LIGHT;PWR;ON>")
    sendEvent(name: "lightSwitch", value: "on")
}

def lightOff() {
    sendCommand("<" + deviceMac + ";LIGHT;PWR;OFF>")
    sendEvent(name: "lightSwitch", value: "off")
}

def lightLevel(Integer level) {
    sendCommand("<" + deviceMac + ";LIGHT;LEVEL;SET;" + level + ">")
    sendEvent(name: "lightLevel", value: level)
}

// Map switch & switch level methods to fan methods

def on() {
    fanOn()
}

def off() {
    fanOff()
}

// Primarily used for Google Assistant voice control. The tile uses fanSpeed().
def setLevel(Integer level) {
    Integer speed = convertPercentToSpeed(level)
    fanSpeed(speed)

    // Set the "level" attribute as SmartThings uses this value to set fan speed
    // when using turn up or turn down voice commands
    sendEvent(name: "level", value: level)
}

// Helper methods

private Integer convertPercentToSpeed(percent) {
    Integer speed = Math.ceil(percent / 100.0 * 7)

    // Make sure fan speed isn't higher than 7
    speed = (speed > 7) ? 7 : speed

    // Don't let fan speed be set to 0
    speed = (speed < 1) ? 1 : speed

    return speed
}

private void createChildDevices() {
    addChildDevice("Haiku Fan Light", "${getDeviceNetworkId()}-light", null, [completedSetup: true, label: "${device.displayName} Light", isComponent: true, componentName: "light", componentLabel: "Light"])
}

private String getDeviceNetworkId() {
    def ipHex = deviceIp.tokenize( '.' ).collect { String.format('%02X', it.toInteger()) }.join()
    def port = 31415
    def portHex = port.toString().format('%04X', port.toInteger())
    return "${ipHex}:${portHex}"
}

private sendCommand(String message) {
    device.deviceNetworkId = getDeviceNetworkId()
    def hubAction = new physicalgraph.device.HubAction(message, physicalgraph.device.Protocol.LAN)
    sendHubCommand(hubAction)
}
