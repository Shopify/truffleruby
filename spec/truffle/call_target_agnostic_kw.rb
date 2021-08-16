# truffleruby_primitives: true

require_relative '../ruby/spec_helper'

def descriptor1(a, b)
  Primitive.keyword_descriptor
end

def descriptor2(a, b:)
  Primitive.keyword_descriptor
end

def descriptor3(a, b: 101, c: 102)
  Primitive.keyword_descriptor
end

describe "Call-target agnostic keyword arguments" do
  describe "pass a descriptor describing static keyword arguments" do
    it "that is empty for simple calls" do
      descriptor1(1, 2).should be_empty
    end

    it "that contains the names of simple keyword arguments" do
      descriptor2(1, b: 2).should == [:b]
    end

    it "that contains the names of present optional keyword arguments" do
      descriptor3(1, b: 2, c: 3).should == [:b, :c]
    end

    it "that does not contain the name of missing optional keyword arguments" do
      descriptor3(1, b: 2).should == [:b]
      descriptor3(1, c: 2).should == [:c]
    end

    it "that does not contain the name of distant explicitly splatted keyword arguments" do
      distant = {c: 3}
      descriptor3(1, b: 2, **distant).should == [:b]
    end

    it "that does not contain the name of near explicitly splatted keyword arguments" do
      descriptor3(1, b: 2, **{c: 3}).should == [:b]
    end

    #it "that does not contain the name of implicitly splatted keyword arguments" do
    #  descriptor3(1, {b: 2, c: 3}).should == [:b]
    #end
  end
end
