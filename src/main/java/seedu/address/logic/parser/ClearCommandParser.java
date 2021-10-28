package seedu.address.logic.parser;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ClearCommand;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EVENT_FLAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PERSON_FLAG;

import seedu.address.logic.parser.exceptions.ParseException;

import java.util.stream.Stream;

public class ClearCommandParser implements Parser<ClearCommand> {
    private static final int PERSON_FLAG = 0;
    private static final int EVENT_FLAG = 1;
    private static final String VALIDATION_REGEX = "^all$|^[0-9]*-[0-9]*$";

    public ClearCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_PERSON_FLAG, PREFIX_EVENT_FLAG);

        boolean isClearingPerson = arePrefixesPresent(argMultimap, PREFIX_PERSON_FLAG);
        boolean isClearingEvent = arePrefixesPresent(argMultimap, PREFIX_EVENT_FLAG);

        assert(!(isClearingEvent && isClearingPerson));

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE));
        } else if (isClearingPerson && isClearingEvent) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE));
        } else if (!isClearingPerson && !isClearingEvent) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE));
        }

        String clearRange;
        int flag;

        if (isClearingPerson) {
            flag = PERSON_FLAG;
            clearRange = argMultimap.getValue(PREFIX_PERSON_FLAG).get();
        } else {
            flag = EVENT_FLAG;
            clearRange = argMultimap.getValue(PREFIX_EVENT_FLAG).get();
        }

        if (!clearRange.matches(VALIDATION_REGEX)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE));
        }

        if (clearRange.equals("all")) {
            return new ClearCommand(flag, 0, null, null);
        } else {
            Index begin;
            Index end;
            String[] range = clearRange.split("-");
            assert(range.length == 2);
            begin = ParserUtil.parseIndex(range[0]);
            end = ParserUtil.parseIndex(range[1]);
            return new ClearCommand(flag, 1, begin, end);
        }
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}

