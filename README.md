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


## Credits
Original proposal by [Cofveve-19](https://www.youtube.com/watch?v=gg6glENUHLQ&feature=emb_title&ab_channel=JamesDearlove)
