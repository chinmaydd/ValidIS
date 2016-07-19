require './demographic.rb'
require 'jaro_winkler'

# Reference: https://www.cs.cmu.edu/~pradeepr/papers/ijcai03.pdf

# Intermediate duplicate matching algorithms deal with fuzzy logic, nickname tables, phoentic encoding and arbitrary and subjective scoring systems!
# Based on research it has been found that the Jaro Winkler distance is a good metric for finding similarity in names. We will be using the same to find the number of duplicate records.
def jaro_winkler_match
    jaro_winkler_query(Demographic).length
end

# Find out similar records based on the Jaro Winkler distance between them
# Params:
# +relation+:: ActiveRecord_Relation/Class on which we will be applying the query
def jaro_winkler_query(relation)
    relation.select{|record| check_distance(record,relation)}
end

# The numerator should include both records when the goal is to measure the overall volume of records that are now in “error” (due to the original and the duplicate record being incomplete for the patient).
# Params:
# +relation+:: ActiveRecord_Relation/Class on which we will be applying the query.
# +record+:: Record which we are comparing with the other ones in the relation
def check_distance(record, relation)
   relation.all do |compared_record|
       return true if normalized_jaro(record, compared_record) >= 0.75
   end
   false
end

# Normalizing the Distance, including the first name and the last name for normalization
# Params:
# +record+:: First record to be compared.
# +compared_record+:: Second record to be compared.
def normalized_jaro(record, compared_record)
    (JaroWinkler.distance(record.first_name, compared_record.first_name) +
     JaroWinkler.distance(record.last_name,  compared_record.last_name))/2
end
