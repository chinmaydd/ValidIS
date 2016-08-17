# This is a startup script to be run when the CIS is first setup with OSCAR.
# It is important that we include this for a simplified Validis workflow.
require 'io/console'
require 'unirest'
require './environment'
require 'socket'

#####################
# Environment setup #
#####################

# Prompts the user to enter a username for their database.
def get_username
    print "Please enter your username for database access: "
    gets.chomp
end

# Prompts a user to input their user password.
# Password is hidden while typing. 1337 feature spotted.
def get_user_password
    print "Please enter your password for database access: "
    # Used to hide the password while typing. 1337 feature!
    STDIN.noecho(&:gets).chomp
end

# Prompts the user to input a database name.
def get_db_name
    puts
    print "Please enter the database name: "
    gets.chomp
end

# Sets up the environment file based on the user details
# Params:
# +username+:: Database username for the given user.
# +password+:: Password for a given user account in the database.
# +db_name+:: Database name used for accessing patient records.
def setup_environment(username, password, db_name)
    File.open("environment.rb", 'a') { |file|
        file.write("ENV['db_user'] = '" + username + "'\n")
        file.write("ENV['db_pass'] = '" + password + "'\n")
        file.write("ENV['db_name'] = '" + db_name  + "'\n")
    }
end

####################################
# Register touchpoint with Validis #
####################################

# Method used for posting the CIS data obtained from the user to the Validis server.
# Uses Unirest API for making a post request and returns a response.
# Params:
# +cis_data+:: Data consisting of information related to the CIS.
def post_to_hub(cis_data)
    Unirest.post ENV['hub_url'],
        headers: {"Content-Type" => "application/json"},
        parameters: cis_data.to_json
end

# A 201 code specifies id if the resource was created.
# Params:
# +response_status+:: Contains the response from the server.
def is_success?(response_status)
    response_status.code == 201
end

# Fetches the ID from the response body.
# Params:
# +response+:: Response obtained from the server.
def get_id(response)
    response.body["id"]
end

# Saves the id in the environment file locally.
# Params:
# +response+:: Contains the response obtained from the (Validis)server.
def save_id(response)
    File.open('environment.rb', 'a') {|file|
        file.write("ENV['id'] = '" + get_id(response) + "'\n")
    }
end

# Registers the touchpoint with the Validis Hub.
# If the registration is successful, the ID is returned, otherwise the error returned from the server is shown.
# Params:
# +cis_data+:: Data containing the API Url, name and address of the CIS.
def register_touchpoint(cis_data)
    response = post_to_hub cis_data
    if is_success? response
        puts "Registration was successful!"
        puts "Your ID is as follows: " + get_id(response)
        save_id response
    else
        puts response.body
    end
end

# Sets the CIS name in the environment file.
def set_cis_name(name)
     File.open('environment.rb', 'a') {|file|
        file.write("ENV['name'] = '" + name + "'\n")
    }
end

# Gets the name of the Clinic from the user.
def get_cis_name
    print "Please enter the name of your clinic: "
    name = gets.chomp
    set_cis_name(name)
    name
end

# Gets the address of the CIS from the user.
def get_cis_address
    print "Please enter the address of the clinic: "
    gets.chomp
end

# Port at which the Validis server will be running.
# Let us assume that we are going to run this Ruby server on 5667
def port
    5667
end

# Generates an API URL for the given system using its IP address and the port at which the ruby server is running.
def get_api_url
    "http://" + Socket::getaddrinfo(Socket.gethostname,"echo",Socket::AF_INET)[0][3] + ":" + port.to_s + "/api"
end

# Returns the CIS data collected from the user as a Hash
def get_cis_data
    {:name    => get_cis_name,
     :address => get_cis_address,
     :api_url => get_api_url}
end

##################
# Main function! #
##################

# Starts up the configuration setup for Validis.
def startup!
    puts "Welcome to the Validis setup!"
    setup_environment   get_username, get_user_password, get_db_name
    register_touchpoint get_cis_data
    puts "We are all done!"
end

startup!
