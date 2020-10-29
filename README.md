<p align="center">
  <img width="400" height="400" src="https://i.imgur.com/yU7Gy0B.png">
</p>

# EVA
Eva, or Electrify Voting Australia, is a flexible and secure electronic voting system designed specifically for Australia. It focuses on providing an intuitive and convenient voting system aimed at reducing rates of informal (cannot be counted for reasons such as not numbering all preferences) voting in Australia.

## Source Code
Source code is provided to the public to examine and identify that there are no security vulnerabilities.

## Configuration
The program is compiled specifically on each machine with a YAML configuration file describing the members and the types of each ballot. Config files are structured as such:
```yaml
ballots:
  candidateList:
    - {name: Tom Brown, party: Example Party}
    name: House of Representatives
    numCandidates: 1
    numVotesNeeded: _
    printMsg: Lower house ballot complete, ballot printing...
```

## Usage

Voters can place votes in on a digital upper house and lower house ballot, both of which will then be printed. At each stage, users are instructed on how to place their preferences. 

Electoral Commission officials can generate ballots with a separate program that is used in command line form.


## Voting Program Screens
# Opening screen 
Shows before and after user has voted 
![Opening Screen](https://i.imgur.com/BHmmp0u.png)

# Lower house voting screen
Allows the user to vote on a simple ballot like the one used for the upper house. Provides a help message, a clear all button for the user to restart voting and clear indication when the user makes errors such as deselecting a preference that isn't their lowest.
![Lower house help message](https://i.imgur.com/Q5oSSiq.png)
![Lower house voting](https://i.imgur.com/OIKM3jq.png)

# Upper house voting screen
A voter can choose to vote above the line for parties or below the line for specific individuals in the senate. It also has the same help abilities as the lower house ballot.
![Upper house help](https://i.imgur.com/ovZgC5J.png)
![Upper house ATL](https://i.imgur.com/DSUZFDl.png)
![Upper house BTL](https://i.imgur.com/jA5Qp4j.png)

# Confirmation screen
After placing preferences, the program asks the user to confirm that their preferences are correct before printing. If there are candidates they have not preferenced they are greyed out to indicate this.
![Confirmation screen](https://i.imgur.com/MzelUCl.png)

# Printing screen
Shows after the voter has confirmed their preferences are correct to indicate that the ballot is printing.
![Printing Screen](https://i.imgur.com/Ltmxnql.png)

## Credits
Original proposal by [Cofveve-19](https://www.youtube.com/watch?v=gg6glENUHLQ&feature=emb_title&ab_channel=JamesDearlove)
