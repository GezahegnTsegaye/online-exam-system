package et.gov.online_exam.dal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import java.util.List;

public class AnswerDto {

    @JsonProperty("name")
    private String name; // answer;
    @JsonProperty("optionChecked")
    private String optionChecked;
    @JsonProperty("optionSelection")
    private String optionSelection;

    @JsonProperty("textAnswer")
    private String textAnswer;


}
