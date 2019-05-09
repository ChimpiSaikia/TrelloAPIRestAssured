import constants.Constants;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class CreateBoardTest extends BaseTest {
    String cardId;

    @Test
    public List<Map<String, Object>> getBoardList() {

        RequestSpecification requestSpecification =
                given()
                        .queryParam("key", keyValue)
                        .queryParam("token", tokenValue)
                        .pathParam("id", boardId)
                        .contentType(ContentType.JSON);

//        RequestSpecification requestSpecification = RestAssured.given();
//        requestSpecification.header("Content-Type", "Application/json");
//        JSONObject requestJson = new JSONObject();
//        try {
//
//            requestJson.put("key", keyValue);
//            requestJson.put("token", tokenValue);
//
//        } catch (JsonException exception) {
//            exception.printStackTrace();
//        }
//      requestSpecification.body(requestJson.toString()).pathParam("boardId", boardId);
//        Response response = requestSpecification.get(GlobalConstants.GET_BOARD_LIST);
//
//        response = response.jsonPath().get();

        Response response = requestSpecification.
                when()
                .get("/boards/{boardId}/lists");
        System.out.println("Board id **********" +boardId );

        response.
                then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .jsonPath().get();

        List<Map<String, Object>> actualList = response.jsonPath().get();
        actualList.size();
        for (int i = 0; i < actualList.size(); i++) {

            // Assert.assertEquals(actualList.get(i).get("name"), getExpectedDataList().get(i));
            Assert.assertTrue((actualList.get(i).containsValue(getExpectedDataList().get(i))));

        }
        return actualList;
    }

    private List<String> getExpectedDataList() {
        List<String> lists = new ArrayList<>();
        lists.add("To Do");
        lists.add("Doing");
        lists.add("Done");
        return lists;
    }

    @Test
    public String createCardInList() {

        String actualCardName = "TestCard";
        String listId = (String) getBoardList().get(1).get("id");
        System.out.println("List Id "+listId);
        RequestSpecification requestSpecification =
                given()
                        .queryParam("key", keyValue)
                        .queryParam("token", tokenValue)
                        .queryParam("name", "TestCard")
                        .queryParam("idList", listId)
                        .contentType(ContentType.JSON);
        Response response = requestSpecification.
                when()
                .post("/cards");

        response.
                then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .jsonPath().getMap("$");

        Map<String, ?> map = response.jsonPath().getMap("$");
        String expectedCardName = (map.get("name").toString());
        Assert.assertEquals(actualCardName,expectedCardName);
        cardId = (map.get("id").toString());
        return cardId;
    }

    @Test
    public void updateCardDetails() {
        //cardId = createCardInList();
        System.out.println("cardId " + cardId);
        String expectedCardName = "New Card Name";
        RequestSpecification requestSpecification =
                given()
                        .queryParam("key", keyValue)
                        .queryParam("token", tokenValue)
//                        .pathParam("id" ,cardId)
                        .queryParam("name", "New Card Name")
                        .queryParam("desc" , "New Description")
                        .contentType(ContentType.JSON);
        Response response = requestSpecification.
                when()
                .put("/cards/{id}");

        response.
                then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .jsonPath().getMap("$");

        Map<String, ?> map = response.jsonPath().getMap("$");
        String actualCardName = (map.get("name").toString());
        Assert.assertEquals(actualCardName, expectedCardName);
    }

    @Test
    public void deleteCard(){

        String cardId = createCardInList();
        RequestSpecification requestSpecification =
               given()
                        .queryParam("key", keyValue)
                        .queryParam("token", tokenValue)
                        .pathParam("id", cardId)
                       .contentType(ContentType.JSON);
        Response response = requestSpecification.
                when()
                .delete("/cards");

       response.
                then()
                .statusCode(200);
        System.out.println("Status code " +response.getStatusCode());

   }

}














