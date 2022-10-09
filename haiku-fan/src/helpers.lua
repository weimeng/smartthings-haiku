local capabilities = require("st.capabilities")
local log = require("log")
local socket = require("socket")

local helpers = {}

function helpers.convert_fan_speed_to_percent(speed)
  return math.floor(speed / 7.0 * 100)
end

function helpers.convert_light_level_to_percent(level)
  return math.floor(level / 16.0 * 100)
end

function helpers.convert_percent_to_fan_speed(percent)
  local speed = math.ceil(percent / 100.0 * 7)

  -- Make sure fan speed isn't higher than 7
  if speed > 7 then return 7 end

  -- Don't let fan speed be set to 0
  if speed < 1 then return 1 end

  return speed
end

function helpers.convert_percent_to_light_level(percent)
  local level = math.ceil(percent / 100.0 * 16)

  -- Make sure light level isn't higher than 16
  if level > 16 then return 16 end

  -- Don't let light level be set to 0
  if level < 1 then return 1 end

  return level
end

function helpers.is_fan(device)
  local device_suffix = string.sub(device.device_network_id, 19, -1)

  if device_suffix == 'FAN' then
    return true
  end

  return false
end

function helpers.refresh_device_state(device)
  local ip_address = device:get_field('ip_address')
  local mac_address = device:get_field('mac_address')

  -- Get device state
  local udp = socket.udp()

  local command = "<" .. mac_address .. ";"
  if helpers.is_fan(device) then
    command = command .. "FAN;SPD;GET;ACTUAL>"
  else
    command = command .. "LIGHT;LEVEL;GET;ACTUAL>"
  end

  udp:sendto(command, ip_address, 31415)

  -- Set device state
  local data, ip, _port = udp:receivefrom()
  if data then
    log.info("-- Device (" .. device.st_store["device_network_id"] .. ") refresh data received - " .. data)

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

function helpers.split_response(response)
  local tmp_data = string.sub(response, 2, -2) .. ';'

  fields = {}
  for field in tmp_data:gmatch("(.-);") do
    table.insert(fields, field)
  end

  return fields
end

return helpers
