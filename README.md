## Change actor behavior in runtime

- Use stash() to stash the messages in the actor, otherwise Operation can only happen after Connect

### FSM
- Same DSL, use when there are more states

### Become/unbecome
- Same DSL, use when there are only two states
- FSM has State, Data
- Extends FSM trait (State, Data)
- same stash() and unstash()