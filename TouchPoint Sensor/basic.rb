require './demographic'
require 'pry'

# Reference: http://library.ahima.org/PB/PatientIdentityHIE#.V40gaHUrJhE

# Calcuates the rate of the data duplication.
def exact_rate
  key = Date.today.strftime('%b %y')
  ret_val = {}
  ret_val[key] = exact_match.fdiv(number_of_records) * 100
  ret_val
  end

# Exact match algorithm returns the total number of records which are duplicates which have the following rules:
# 1. They have the "exact" same first_name.
# 2. They have the "exact" same last_name.
# 3. They have the "exact" same sin number.
# In the case of BC Clinics, they use the HIN number as their unique idetntification which also corresponds to their MRP.
# Also, gender can be considered for special cases. But for most searches, it is not needed.
def exact_match
    exact_query(Demographic).length
end

# Returns the total number of records in the database.
def number_of_records
  Demographic.count
end

# The determininstic approach relies on the fact that the unique identifier in enough for spotting duplicates and other fields are not really required.
# OSCAR uses the HIN number or also in this case (i.e BC) the MRP number.
# There are also a lot of cases wherein the HIN number is kept nil. We need to filter those records out.
def deterministic_match
    deterministic_query(Demographic).length
end

# Finds out the exactly similar records
# Params:
# +relation+:: ActiveRecord_Relation/Class on which we will be applying the query
def exact_query(relation)
    relation.select(:first_name, :last_name, :sin).group(:first_name, :last_name, :date_of_birth, :month_of_birth, :year_of_birth, :sin).having("count(*) > 1")
end

# Finds out records which have the same unique identifier.
# In this case the hin number.
# Params:
# +relation+:: ActiveRecord_Relation/Class on which we will be applying the query
def deterministic_query(relation)
    relation.select(:first_name, :last_name, :hin).group(:hin).having("(count(*) > 1) and (hin != ?)", "")
end
