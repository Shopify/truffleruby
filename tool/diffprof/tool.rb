require 'zlib'
require 'json'

mri_filename, tr_filename, * = ARGV

# The main data structure is a tree of activations with method name, source
# location, and the number of times this activation was sampled in both
# MRI and TruffleRuby.

Activation = Struct.new(:name, :file, :line, :mri, :truffle, :children)

# There's a root activation

main_activation = Activation.new('<main>', nil, nil, 0, 0, [])

# Build a reference tree from the MRI log

main_thread = nil
File.open(mri_filename) do |mri_file|
  state = :first_line
  Zlib::GzipReader.new(mri_file).each_line do |line|
    line.rstrip!
    case state
    when :first_line
      raise unless line == 'rbspy02'
      state = :header
    when :header
      state = :first_sample
    when :first_sample, :samples
      sample = JSON.parse(line)

      # Ignore everything except the main thread, which we determine from the first sample
      thread = sample['thread_id']
      if state == :first_sample
        main_thread = thread
      else
        next unless thread == main_thread
      end
      
      # Go through each activation in this sample
      previous_activation = nil
      sample['trace'].reverse.each do |trace|
        if previous_activation
          # Mangle the name
          name = trace['name']
          next if name == '(unknown) [c function]'
          name.gsub!(/ \[c function\]/, '')
          name = 'block' if name.start_with?('block ')

          # If there already an activation in the tree for this activation?
          child_activation = nil
          previous_activation.children.each do |child|
            if name == child.name
              child_activation = child
              break
            end
          end

          if child_activation
            # Record we saw this activation again in MRI
            child_activation.mri += 1
          else
            # Not seen this activation before - create a new one
            file = trace['relative_path']
            file = nil if file == '(unknown)'
            if file
              line = trace['lineno']
            else
              line = nil
            end
            child_activation = Activation.new(name, file, line, 1, 0, [])
            previous_activation.children.push child_activation
          end

          previous_activation = child_activation
        else
          main_activation.mri += 1
          previous_activation = main_activation
        end
      end
      state = :samples if state == :first_sample
    else
      raise
    end
  end
end

# Try to match the TruffleRuby log against the MRI tree

def match_activation(sample, activation)
  # Record we also saw this activation in TruffleRuby
  activation.truffle += sample['hit_count']

  sample['children'].each do |child|
    # Mangle the name
    name = child['root_name']
    name = 'block' if name.start_with?('block ')
    name = name.split('#').last
    name = name.split('.').last

    # We now need to do a best effort search for the right activation...
    match = nil
    activation.children.each do |candidate|
      if candidate.name == name
        match = candidate
        break
      end
    end

    if match
      # Got one - so recurse...
      match_activation child, match
    else
      # Kludge - Control-C on TruffleRuby creates lots of stack activations while it handles it...
      return if name == 'raise'

      # Print some debug output and try to fix if you care about this part of call stack...
      puts 'no match!'
      puts name
      activation.children.each do |candidate|
        puts candidate.name
      end
    end
  end
end

JSON.parse(File.read(tr_filename), max_nesting: false)['profile'].each do |thread|
  next unless thread['thread'].start_with?('Thread[main,')
  sample = thread['samples'].first
  match_activation sample, main_activation
end

# Print the tree of activations with samples from both implementations

def print_activation(indentation, activation, main_activation)
  printf '%5.1f%%', activation.mri / main_activation.mri.to_f * 100
  print '  '
  printf '%5.1f%%', activation.truffle / main_activation.truffle.to_f * 100
  print ' ' * indentation
  print '  '
  print activation.name
  print '  '
  if activation.file
    print activation.file 
    print ':'
    print activation.line
  end
  puts
  activation.children.each do |child|
    print_activation indentation + 1, child, main_activation
  end
end

print_activation 0, main_activation, main_activation
