<%
org.jruby.embed.ScriptingContainer ruby = new org.jruby.embed.IsolatedScriptingContainer();

Object result = ruby.runScriptlet( "require 'hello_world';HelloWorld.new" ).toString();

Object gem = ruby.runScriptlet( "require 'virtus';Gem.loaded_specs['backports'].gem_dir" ).toString();
%>
<html>
<body>
<h2><%= result %></h2>
<h2><%= gem %></h2>
</body>
</html>
