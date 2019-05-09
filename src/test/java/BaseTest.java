import constants.Constants;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.annotations.*;

import static io.restassured.RestAssured.given;

public class BaseTest {

    String keyValue = "0697ace29da135af1009cc535346c753";
    String tokenValue = "7e0b2389afbcf0c9cd08521ee285c1d07edc56da5173b652572920748c0a3970";

    protected String boardId;
    public String boardName = "BoardRestAssured";
    public JSONObject jsonObject = new JSONObject();

    @BeforeSuite
    public void setup() {

        RestAssured.baseURI = Constants.baseURL;

        jsonObject.put("key", keyValue);
        jsonObject.put("token", keyValue);

    }

    @BeforeClass
    public void createBoard() {
        Response response =

                given()
                        .queryParam("name", boardName)
                        .contentType(ContentType.JSON)
                        .body(jsonObject.toJSONString())
                        .log().all().


                        when()
                        .post(Constants.createBoard);

        response
                .then()
           //     .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().body().jsonPath().get("name").equals(boardName);

                boardId = (String) response.then()
                .extract().jsonPath().getMap("$").get("id");
    }

    @AfterClass
    public void tearDown()
    {
        //Delete board after running the suite
        given()
                .body(jsonObject.toJSONString())
                .pathParam("id",boardId)
                .contentType(ContentType.JSON).log().all().

                when()
                .delete(Constants.deleteBoard).

                then()
                .statusCode(200)
                .contentType(ContentType.JSON);

    }

}

