package psm.mywallet.client.android;


import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import psm.mywallet.api.EntryDTO;

/**
 * @author Adrian Michalski
 */
public class ListEntryParser {

    private static final String ACCOUNT_OPERATION_REGEX = "([0-9]+(\\*))?(-?[0-9]+((\\.|,)?[0-9]+)?)";

    private Pattern accountOperationPattern = Pattern.compile(ACCOUNT_OPERATION_REGEX);

    public List<EntryDTO> parse(String pCommandString) {
        Log.d("ListEntryParser", "Starting to parse \"" + pCommandString + "\"");

        List<String> words = splitIntoWords(pCommandString);
        Log.d("ListEntryParser", "Splitted into :" + words);

        Set<String> hashTags = findHashTags(words);
        Log.d("ListEntryParser", "HashTags found: " + hashTags);

        List<BigDecimal> operationValues = findOperationValues(words);
        Log.d("ListEntryParser", "Operations found: " + operationValues + "\n\n");

        ImmutableList.Builder<EntryDTO> entries = ImmutableList.builder();
        for (BigDecimal operation : operationValues) {
            EntryDTO entryDTO = new EntryDTO();
            entryDTO.setDescription(pCommandString);
            entryDTO.setTags(hashTags);
            entryDTO.setValue(operation);
            entries.add(entryDTO);
        }
        return entries.build();
    }

    private List<String> splitIntoWords(String pCommandString) {
        String noDuplicatedWhitespaces = pCommandString.replaceAll("\\s+", " ");
        return ImmutableList.copyOf(noDuplicatedWhitespaces.split(" "));
    }

    private Set<String> findHashTags(List<String> pWords) {
        ImmutableSet.Builder<String> hashTags = ImmutableSet.builder();
        for (String word : pWords) {
            if (word.startsWith("#")) {
                String wordWithoutHashCharacter = word.replace("#", "");
                hashTags.add(wordWithoutHashCharacter);
            }
        }
        return hashTags.build();
    }

    private List<BigDecimal> findOperationValues(List<String> pWords) {
        ImmutableList.Builder<BigDecimal> operations = ImmutableList.builder();
        for (String word : pWords) {
            if (accountOperationPattern.matcher(word).matches()) {
                operations.add(mapOperationStringToValue(word));
            }
        }
        return operations.build();
    }

    private BigDecimal mapOperationStringToValue(String pOperation) {
        String unifiedFloatingPoint = pOperation.replaceAll(",", ".");

        if (unifiedFloatingPoint.contains("*")) {
            ImmutableList<String> numbers = ImmutableList.copyOf(unifiedFloatingPoint.split("\\*"));

            Preconditions.checkArgument(numbers.size() <= 2, "Operation cannot have more than one multiplication");

            BigDecimal result = BigDecimal.ONE;
            for (String number : numbers) {
                BigDecimal parsedNumber = new BigDecimal(number);
                result = result.multiply(parsedNumber);

            }
            return result;
        } else {
            return new BigDecimal(unifiedFloatingPoint);
        }
    }

}
