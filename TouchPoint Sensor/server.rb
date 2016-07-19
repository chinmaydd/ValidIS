require 'sinatra'
require 'json'
require 'sinatra/cross_origin'

# Enables cross origin requests. Useful for local testing.
configure do
    enable :cross_origin
end

# Main method which will be exposed through the API provided.
# The Validis hub will make a request to invoke this method and collect duplicate record creation rates.
def collect_data
    {:message => "OMG_WHAT"}
end

# For Validis!
get '/api' do
    content_type :json
    collect_data.to_json
end

# We will be coding up a simple web server in Sinatra which accepts requests to an API endpoint for
# sending the total count of duplicate patient records. This server will act as a request-response model
# rather than a continuous monitoring
get '/info' do
    return "Hello, world"
end
