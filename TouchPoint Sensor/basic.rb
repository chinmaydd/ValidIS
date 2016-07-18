require './demographic'
require 'pry'

# Reference: http://library.ahima.org/PB/PatientIdentityHIE#.V40gaHUrJhE

# Exact match algorithm returns the total number of records which are duplicates which have the following rules:
# 1. They have the "exact" same first_name.
# 2. They have the "exact" same last_name.
# 3. They have the "exact" same sin number.
# In the case of BC Clinics, they use the HIN number as their unique idetntification which also corresponds to their MRP.
# Also, gender can be considered for special cases. But for most searches, it is not needed.
def exact_match
    exact_query.length
end

# The determininstic approach relies on the fact that the unique identifier in enough for spotting duplicates and other fields are not really required.
# OSCAR uses the HIN number or also in this case (i.e BC) the MRP number.
# There are also a lot of cases wherein the HIN number is kept nil. We need to filter those records out.
def deterministic_match
    deterministic_query.length
end

def exact_query
    Demographic.select(:first_name, :last_name, :sin).group(:first_name, :last_name, :sin).having("count(*) > 1")
end

def deterministic_query
    Demographic.select(:first_name, :last_name, :hin).group(:hin).having("(count(*) > 1) and (hin != ?)", "")
end
