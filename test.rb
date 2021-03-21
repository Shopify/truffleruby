struct = Struct.new(:foo).new(14)

begin
  p struct[:bar]
rescue IndexError, NameError
  p 'rescued'
  p $!
end

p $!
