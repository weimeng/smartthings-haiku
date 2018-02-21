/**
 * Haiku Fan Light Control
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
 */

metadata {
	definition (name: "Haiku Fan Light", namespace: "weimeng", author: "Wei-Meng Lee") {
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