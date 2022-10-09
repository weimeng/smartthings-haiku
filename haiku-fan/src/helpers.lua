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

function helpers.split_response(response)
  local tmp_data = string.sub(response, 2, -2) .. ';'

  fields = {}
  for field in tmp_data:gmatch("(.-);") do
    table.insert(fields, field)
  end

  return fields
end

return helpers
