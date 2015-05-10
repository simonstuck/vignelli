#!/usr/bin/env ruby -w

#
# Collects classifications statistics from a classifications JSON file.
#

require 'optparse'
require 'json'

filename = nil;

optparse = OptionParser.new do |opts|
	opts.on('-h', '--help', 'Display this screen.') do 
		puts opts
		exit
	end

	opts.on('-f FILE','--file FILE','The classifications file to read.') do |f|
		filename = f
	end
end

optparse.parse!

if filename.nil? then
	puts optparse
	exit
end


file = File.read(filename)

metrics_hash = JSON.parse(file)

puts "Statistics for #{metrics_hash['name']}:"
puts "---------------------------------------"


class_classifications = metrics_hash['classMethodClassificationsMap']

num_classes = class_classifications.size
num_methods = class_classifications.values.map { |c| c['methodClassifications'].size }.inject(:+)

puts "Number of Classes Analysed: #{num_classes}"
puts "Number of Methods Analysed: #{num_methods}"
