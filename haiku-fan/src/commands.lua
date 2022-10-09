local capabilities = require("st.capabilities")
local socket = require("socket")

local command_handler = {}

function command_handler.switch_on(_driver, device, command)
  if command_handler.is_fan(device) then
    command_handler.send_command("FAN;PWR;ON", device)
  else
    command_handler.send_command("LIGHT;PWR;ON", device)
  end

  device:emit_event(capabilities.switch.switch.on())
end

function command_handler.switch_off(_driver, device, command)
  if command_handler.is_fan(device) then
    command_handler.send_command("FAN;PWR;OFF", device)
  else
    command_handler.send_command("LIGHT;PWR;OFF", device)
  end

  device:emit_event(capabilities.switch.switch.off())
end

function command_handler.set_level(_driver, device, command)
  if command_handler.is_fan(device) then
    local speed = command_handler.convert_percent_to_fan_speed(command.args.level)
    command_handler.send_command("FAN;SPD;SET;" .. speed, device)
  else
    local level = command_handler.convert_percent_to_light_level(command.args.level)
    command_handler.send_command("LIGHT;LEVEL;SET;" .. level, device)
  end

  device:emit_event(capabilities.switchLevel.level(command.args.level))
end

function command_handler.is_fan(device)
  local device_suffix = string.sub(device.device_network_id, 19, -1)

  if device_suffix == 'FAN' then
    return true
  end

  return false
end

function command_handler.send_command(message, device)
  local device_ip = device:get_field('ip_address')
  local device_mac = device:get_field('mac_address')

  local udp = socket.udp()
  udp:sendto("<" .. device_mac .. ";" .. message .. ">", device_ip, 31415)
end

function command_handler.convert_percent_to_fan_speed(percent)
  local speed = math.ceil(percent / 100.0 * 7)

  -- Make sure fan speed isn't higher than 7
  if speed > 7 then return 7 end

  -- Don't let fan speed be set to 0
  if speed < 1 then return 1 end

  return speed
end

function command_handler.convert_percent_to_light_level(percent)
  local level = math.ceil(percent / 100.0 * 16)

  -- Make sure light level isn't higher than 16
  if level > 16 then return 16 end

  -- Don't let light level be set to 0
  if level < 1 then return 1 end

  return level
end

return command_handler
