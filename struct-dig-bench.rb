require 'benchmark/ips'

puts RUBY_DESCRIPTION

struct = Struct.new(:foo).new(14)

Benchmark.ips do |x|
  x.report('found') do
      struct[:foo]
  end

  x.report('not-found') do
    struct[:bar]
  rescue IndexError, NameError
    nil
  end
end
