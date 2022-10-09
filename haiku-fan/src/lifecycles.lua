local capabilities = require("st.capabilities")
local log = require('log')
local socket = require("socket")

-- Local imports
local helpers = require("helpers")

local lifecycle_handler = {}

function lifecycle_handler.init(driver, device)
  local ip_address = device.st_store["vendor_provided_label"]
  local mac_address = string.sub(device.st_store["device_network_id"], 1, -5)

  -- Set up device MAC and IP address
  device:set_field('ip_address', ip_address)
  device:set_field('mac_address', mac_address)

  -- Get initial device state
  local udp = socket.udp()

  local command = "<" .. mac_address .. ";"
  if helpers.is_fan(device) then
    command = command .. "FAN;SPD;GET;ACTUAL>"
  else
    command = command .. "LIGHT;LEVEL;GET;ACTUAL>"
  end

  udp:sendto(command, ip_address, 31415)

  -- Set initial device state
  local data, ip, _port = udp:receivefrom()
  if data then
    log.info("-- Device (" .. device.st_store["device_network_id"] .. ") Init Data Received - " .. data)

    local level = tonumber(helpers.split_response(data)[5])

    if level == 0 then
      device:emit_event(capabilities.switch.switch.off())
      device:emit_event(capabilities.switchLevel.level(0))
    else
      device:emit_event(capabilities.switch.switch.on())

      local converted_level
      if helpers.is_fan(device) then
        converted_level = helpers.convert_fan_speed_to_percent(level)
      else
        converted_level = helpers.convert_light_level_to_percent(level)
      end

      device:emit_event(capabilities.switchLevel.level(converted_level))
    end
  end

  udp:close()
end

-- function lifecycle_handler.added(driver, device)
-- end

-- function lifecycle_handler.doConfigure(driver, device)
-- end

-- function lifecycle_handler.driverSwitches(driver, device)
-- end

-- function lifecycle_handler.removed(driver, device)
-- end

return lifecycle_handler
