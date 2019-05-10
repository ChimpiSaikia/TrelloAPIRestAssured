import constants.Constants;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.annotations.*;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class BaseTest {

    String keyValue = "";
    String tokenValue = "89a595d48ea16d2769491b290fdcb094d13cfdf5a0ee4cc0d162e5b88c5a0750";

    protected String boardId;
    public String boardName = "Board RestAssured";
    public JSONObject queryParam = new JSONObject();
    @BeforeSuite
    public void setup() {

        RestAssured.baseURI = Constants.baseURL;

        queryParam.put("key", keyValue);
        queryParam.put("token", keyValue);

    }

    @BeforeClass
    public void createBoard() {
        Response response =

                given()
                        .queryParam("name", boardName)
                        .contentType(ContentType.JSON)
                        .body(queryParam.toJSONString())
                        .log().all().
                        when()
                        .post(Constants.createBoard);

        response.then()
                .statusCode(200)
                .contentType(ContentType.JSON)
               // .body(matchesJsonSchemaInClasspath("createBoard.json"))
                .extract().body().jsonPath().get("name").equals(boardName);

        boardId = (String) response.then()
                .extract().jsonPath().getMap("$").get("id");
    }

    @AfterClass
    public void tearDown()
    {
        //Delete board after running the suite
        given()
                .body(queryParam.toJSONString())
                .pathParam("id",boardId)
                .contentType(ContentType.JSON).log().all().

                when()
                .delete(Constants.deleteBoard).

                then()
                .statusCode(200)
                .contentType(ContentType.JSON);

    }

}
