# This is a startup script to be run when the CIS is first setup with OSCAR.
# It is important that we include this for a simplified Validis workflow.
require 'io/console'
require 'unirest'
require './environment'
require 'socket'
require 'pry'

#####################
# Environment setup #
#####################

def get_username
    print "Please enter your username for database access: "
    gets.chomp
end

def get_user_password
    print "Please enter your password for database access: "
    # Used to hide the password while typing. 1337 feature!
    STDIN.noecho(&:gets).chomp
end

def get_db_name
    puts
    print "Please enter the database name: "
    gets.chomp
end

def setup_environment username, password, db_name
    File.open("environment.rb", 'a') { |file|
        file.write("ENV['db_user'] = '" + username + "'\n")
        file.write("ENV['db_pass'] = '" + password + "'\n")
        file.write("ENV['db_name'] = '" + db_name  + "'\n")
    }
end

####################################
# Register touchpoint with Validis #
####################################

def post_to_hub cis_data
    Unirest.post ENV['hub_url'],
        headers: {"Content-Type" => "application/json"},
        parameters: cis_data.to_json
end

# A 201 code specifies id if the resource was created.
def is_success? response_status
    response_status.code == 201
end

def get_id response
    response.body["id"]
end

def save_id response
    File.open('environment.rb', 'a') {|file|
        file.write("ENV['id'] = '" + get_id(response) + "'\n")
    }
end

def register_touchpoint cis_data
    response = post_to_hub cis_data
    if is_success? response
        puts "Registration was successful!"
        puts "Your ID is as follows: " + get_id(response)
        save_id response
    else
        puts response_status.body
    end
end

def get_cis_name
    print "Please enter the name of your clinic: "
    gets.chomp
end

def get_cis_address
    print "Please enter the address of the clinic: "
    gets.chomp
end

# Let us assume that we are going to run this Ruby server on 5667
def port
    5667
end

def get_api_url
    Socket::getaddrinfo(Socket.gethostname,"echo",Socket::AF_INET)[0][3] + ":" + port.to_s + "/api/"
end

def get_cis_data
    {:name    => get_cis_name,
     :address => get_cis_address,
     :api_url => get_api_url}
end

##################
# Main function! #
##################

def startup!
    puts "Welcome to the Validis setup!"
    setup_environment   get_username, get_user_password, get_db_name
    register_touchpoint get_cis_data
    puts "We are all done!"
end

startup!
