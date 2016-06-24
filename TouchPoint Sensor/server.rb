require 'sinatra'
require 'json'

# We will be coding up a simple web server in Sinatra which accepts requests to an API endpoint for
# sending the total count of duplicate patient records. This server will act as a request-response model
# rather than a continuous monitoring

get '/info' do
    return "Hello, world"
end
