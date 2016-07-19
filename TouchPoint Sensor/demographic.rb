require 'active_record'
require './environment'
require './basic'

# Setting up ActiveRecord

# Load environment variables
@db_host = ENV['db_host']
@db_user = ENV['db_user']
@db_pass = ENV['db_pass']
@db_name = ENV['db_name']

# Estabilish a connection to the underlying database based on the environment variables.
ActiveRecord::Base.establish_connection( :adapter  => "mysql2",
                                         :host     => @db_host,
                                         :username => @db_user,
                                         :password => @db_pass,
                                         :database => @db_name )

ActiveRecord::Base.pluralize_table_names = false

# A lot of the searches depend on the fact that we can have plaintext searches for the data in the tables, which is currently not available.
# There might be errors when testing the data.
class Demographic < ActiveRecord::Base
end
