# frozen_string_literal: true

require 'benchmark/ips'
puts RUBY_DESCRIPTION

# rubocop:disable Metrics/ParameterLists

class A 
  def keyword_with_defaults(a: 1, b: 2, c: 3)
    a + b + c
  end

  def keyword_without_defaults(a:, b:, c:)
    a + b + c
  end

  def positional_without_defaults(a, b, c)
    a + b + c
  end
end

class B
  def keyword_with_defaults(c: 18, b: 19, a: 20)
    a + b + c
  end

  def keyword_without_defaults(c:, b:, a:)
    a + b + c
  end

  def positional_without_defaults(a, b, c)
    a + b + c
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
  x.report('Monomorphic, keyword with default args') do
    obj_a.keyword_with_defaults
  end

  x.report('Monomorphic, keyword without default args') do
    obj_a.keyword_without_defaults(a: 1, b: 2, c: 3)
  end

  x.report('Monomorphic, positional without default args') do
    obj_b.positional_without_defaults(1, 2, 3)
  end

  x.report('Polymorphic, keyword with default args') do
    obj_a_or_b.keyword_with_defaults
  end

  x.report('Polymorphic, keyword without default args') do
    obj_a_or_b.keyword_without_defaults(a: 1, b: 2, c: 3)
  end

  x.report('Polymorphic, positional without default args') do
    obj_a_or_b.positional_without_defaults(1, 2, 3)
  end
end
