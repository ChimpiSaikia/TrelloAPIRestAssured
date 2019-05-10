import constants.Constants;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.*;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.testng.Assert.assertEquals;

public class TrelloTest extends BaseTest{

    public ArrayList<String> listIds= new ArrayList<String>();
    String cardId;

    @Test( priority = 1)
    public void validateListsFromBoard()
    {
        ArrayList<String> expectedListNames = new ArrayList<String>();

        expectedListNames.add("To Do");
        expectedListNames.add("Doing");
        expectedListNames.add("Done");

        List<HashMap<String,String>> lists =

                given()

                        .pathParam("id", boardId)
                        .queryParam("key", keyValue)
                        .queryParam("token", tokenValue)
                        .contentType(ContentType.JSON).log().all().
                        when()
                        .get(Constants.getListsInBoard).
                        then()
                        .body("[0].name",equalTo(expectedListNames.get(0)))
                        .extract().response().jsonPath().getList("$");

        //assertEquals(getListsFromMap(lists,"name") , expectedListNames);
        listIds = getListsFromMap(lists,"id");

        System.out.println(listIds.get(0));

    }

    @Test( priority = 2)
    public void createAListAndValidate()
    {
        String listName = "NewListFromAutomation";
        given()
                .body(queryParam.toJSONString())
                .queryParam("name",listName)
                .queryParam("idBoard",boardId)
                .contentType(ContentType.JSON)
                .log().all().
                when()
                .post(Constants.getLists).
                then()
                .statusCode(200)
                .body("name",equalTo(listName))
                .body("idBoard",equalTo(boardId));
    }

    @Test(priority=3)
    public void validateCreateCardinList ()
    {
        String cardName ="My first Card";

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("name", cardName);
        jsonBody.put("idList", listIds.get(0));
        jsonBody.put("key", keyValue);
        jsonBody.put("token", tokenValue);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(jsonBody.toJSONString())
                        .log().all().
                        when()
                        .post(Constants.createCard).
                        then()
                        .statusCode(200)
                        .body("name", equalTo(cardName))
                        .body("idBoard",equalTo(boardId))
                        .body("idList",equalTo(listIds.get(0)))
                        .body(matchesJsonSchemaInClasspath("createCardSchema.json"))
                        .extract()
                        .response();

        //assertEquals(response.statusCode(),200);
        Map<String,?> responseMap = response.getBody().jsonPath().getMap("$");

        // assertEquals(responseMap.get("name"), cardName);
        // assertEquals(responseMap.get("idBoard"),boardId);
        // assertEquals(responseMap.get("idList"),listIds.get(0));

        cardId = (String) responseMap.get("id");

    }

    @Test(priority = 4)
    public void ValidateUpdateCard()
    {
        String updateCardName ="Updated name for Card";

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("name", updateCardName);
        jsonBody.put("idList", listIds.get(0));
        jsonBody.put("key", keyValue);
        jsonBody.put("token", tokenValue);

        Response response =

                given()
                        .contentType(ContentType.JSON)
                        .body(jsonBody)
                        .pathParam("cardId", cardId)
                        .log().all().
                        when()
                        .put(Constants.updateCard).
                        then()
                        .statusCode(200)
                        .body("name", equalTo(updateCardName))
                        .body("badges.location", equalTo(false))
                        .extract()
                        .response();

        // assertEquals(response.statusCode(),200);
        // assertEquals(response.jsonPath().getMap("$").get("name"), updateCardName);

    }

    @Test(priority = 5)
    public void ValidateCardDeleted()
    {
        RequestSpecification requestSpec =  given()
                .body(queryParam.toJSONString())
                .pathParam("cardId", cardId)
                .contentType(ContentType.JSON);

        requestSpec.when()
                .delete(Constants.updateCard).
                then()
                .statusCode(200);

        requestSpec.when()
                .delete(Constants.updateCard).
                then()
                .statusCode(404)
                .extract().body().asString().equals("The requested resource was not found.");


    }

    public ArrayList<String> getListsFromMap (List<HashMap<String,String>> masterList, String key )
    {
        ArrayList<String>  lists = new ArrayList<String>();
        for ( HashMap<String, String> map : masterList)
        {
            lists.add(map.get(key));
        }
        return lists;
    }

}
