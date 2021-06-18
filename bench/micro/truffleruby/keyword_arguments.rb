# frozen_string_literal: true

require 'benchmark/ips'
puts RUBY_DESCRIPTION

# rubocop:disable Metrics/ParameterLists

class A 
  def with_defaults(a: 1, b: 2, c: 3, d: 4, e: 5, f: 6, g: 7, h: 8, i: 9, j: 10)
    a + b + c + d + e + f + g + h + i + j
  end

  def without_defaults(a, b, c, d, e, f, g, h, i, j)
    a + b + c + d + e + f + g + h + i + j
  end
end

class B
  def with_defaults(j: 11, i: 12, h: 13, g: 14, f: 15, e: 16, d: 17,
    c: 18, b: 19, a: 20)
      a + b + c + d + e + f + g + h + i + j
  end

  def without_defaults(a, b, c, d, e, f, g, h, i, j)
    a + b + c + d + e + f + g + h + i + j
  end
end

obj_a = A.new
obj_b = B.new

def obj_a_or_b
  x = Random.new
  case x.rand(2)
  when 0
    A.new
  when 1
    B.new
  end
end

Benchmark.ips do |x|
  x.report('Monomorphic, with default args') do
    obj_a.with_defaults
  end

  x.report('Monomorphic, without default args') do
    obj_b.without_defaults(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
  end

  x.report('Polymorphic, with default args') do
    obj_a_or_b.with_defaults
  end

  x.report('Polymorphic, without default args') do
    obj_a_or_b.without_defaults(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
  end
end
