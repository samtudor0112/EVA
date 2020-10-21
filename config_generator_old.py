def get_input(str_msg, default_value):
    out = input("{} (default: {}): ".format(str_msg, default_value))
    return (out if out else default_value)

def make_config():
    print("Welcome to the config file maker. For a default value, type nothing then enter.")
    directory = get_input("Enter a directory for the config file", "config/config.txt")
    with open(directory, "w") as config:
        num_ballots = get_input("Enter the number of ballots for the election", "1")
        config.write(num_ballots + "\n")
        for ballot_num in range(int(num_ballots)):
            ballot_name = get_input("Enter the name of ballot {}".format(ballot_num + 1), "ballot{}".format(ballot_num + 1))
            config.write(ballot_name + ":")
            num_candidates = get_input("Enter the number of candidates on {}".format(ballot_name), "1")
            config.write(num_candidates + ":")
            num_candidates_required = get_input("Enter the number of candidates required for a valid vote on {}".format(ballot_name), "1")
            config.write(num_candidates_required + ":")
            for candidate_num in range(int(num_candidates)):
                candidate_name = get_input("Enter the name of candidate {}".format(candidate_num + 1), "Jim Bob")
                config.write(candidate_name + "~")
                candidate_party = get_input("Enter {}'s party".format(candidate_name), "Labour")
                config.write(candidate_party + "|")
            config.write("\n")
        print("Done! Config file generated")
            
                

if __name__ == "__main__":
    make_config()
