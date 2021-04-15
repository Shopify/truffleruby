def foo
  10.times do
    bar
  end
end

def bar
  GC.stat
  6000.times do
    baz(rand(10), rand(10), rand(10))
  end
end

def baz(a, b, c)
  [a, b, c].sort[1]
end

loop do
  foo
end
