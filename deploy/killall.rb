#!/usr/bin/env ruby

$: << File.dirname(__FILE__)

require 'comet_config'
require 'yaml'
require 'erb'

config_file = ARGV.first

raise "You must specifcy deployment configuration file." unless config_file
raise "Deployment configuration file #{config_file} does not exist" unless File.exist?(config_file)

@basedir = File.dirname(__FILE__)
config = CometConfig.new(YAML::load(ERB.new(File.read(config_file)).result(binding)))

if config.start_bootstrap
  Process.fork do
    Kernel.exec("ssh", "-o StrictHostKeyChecking=no",
      config.userat + config.bootstrap, "bash",
      File.join(config.rwd, "kill.sh"),
      config.rwd)
  end
end

config.nodes.each do |host|
  config.ports.each do |port|
    Process.fork do
      Kernel.exec("ssh", "-o StrictHostKeyChecking=no",
        config.userat + host, "bash",
        File.join(config.rwd, "kill.sh"),
        config.rwd)
    end
  end
end

Process.waitall