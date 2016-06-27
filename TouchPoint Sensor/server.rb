require 'sinatra'
require 'json'
require 'mysql2'
require 'pry'
require './environment'

# Load environment variables
@db_host = ENV['db_user']
@db_pass = ENV['db_pass']
@db_name = ENV['db_name']

def connect_to_db
    client = Mysql2::Client.new(:host => @db_host, :username => @db_user, :password => @db_pass, :database => @db_name)
end

# We will be coding up a simple web server in Sinatra which accepts requests to an API endpoint for
# sending the total count of duplicate patient records. This server will act as a request-response model
# rather than a continuous monitoring

get '/info' do
    return "Hello, world"
end
