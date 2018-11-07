#!/usr/bin/env ruby

body_parts = []
images = []
scales = []
adjectives = []

music_silent = false
dance_silent = false

5.times do |n|
  if !music_silent && (rand() < 0.20 || n == 4)
    music_silent=true
    scales << "SILENCE"
    adjectives << "SILENCE"
  else
    while true
      scale = `cat /tmp/music-scale | shuf -n1`.chomp
      if !scales.include? scale
        scales << scale
        break
      end
    end

    while true
      adjective = `cat /tmp/music-adjectives | shuf -n1`.chomp
      if !adjectives.include? adjective
        adjectives << adjective
        break
      end
    end
  end

  if !dance_silent && (rand() < 0.20 || n == 4)
    dance_silent=true
    body_parts << "SILENCE"
    images << "SILENCE"
  else
    while true
      body_part = `cat /tmp/dance-body-part | shuf -n1`.chomp
      if !body_parts.include? body_part
        body_parts << body_part
        break
      end
    end

    while true
      image = `cat /tmp/dance-images | shuf -n1`.chomp
      if !images.include? image
        images << image
        break
      end
    end
  end

  seconds = 10 + rand(110)

  puts "MOVEMENT #{n} (#{seconds} seconds)"
  puts "Dance: #{body_part} - #{image}"
  puts "Music: #{scale} - #{adjective}"
  puts
end
