local capabilities = require("st.capabilities")
local socket = require("socket")

-- Local imports
local helpers = require("helpers")

local command_handler = {}

function command_handler.switch_on(_driver, device, _command)
  local message
  if helpers.is_fan(device) then
    message = "FAN;PWR;ON"
  else
    message = "LIGHT;PWR;ON"
  end

  command_handler.send_command(message, device)
  device:emit_event(capabilities.switch.switch.on())
end

function command_handler.switch_off(_driver, device, _command)
  local message
  if helpers.is_fan(device) then
    message = "FAN;PWR;OFF"
  else
    message = "LIGHT;PWR;OFF"
  end

  command_handler.send_command(message, device)
  device:emit_event(capabilities.switch.switch.off())
end

function command_handler.set_level(_driver, device, command)
  local level = command.args.level

  local message
  if helpers.is_fan(device) then
    message = "FAN;SPD;SET;" .. helpers.convert_percent_to_fan_speed(level)
  else
    message = "LIGHT;LEVEL;SET;" .. helpers.convert_percent_to_light_level(level)
  end

  command_handler.send_command(message, device)
  device:emit_event(capabilities.switchLevel.level(level))
end

function command_handler.send_command(message, device)
  local ip_address = device:get_field('ip_address')
  local mac_address = device:get_field('mac_address')

  local udp = socket.udp()
  udp:sendto("<" .. mac_address .. ";" .. message .. ">", ip_address, 31415)
  udp:close()
end

return command_handler
