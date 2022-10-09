local helpers = {}

function helpers.convert_fan_speed_to_percent(speed)
  return speed / 7.0 * 100
end

function helpers.convert_light_level_to_percent(level)
  return level / 16.0 * 100
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
