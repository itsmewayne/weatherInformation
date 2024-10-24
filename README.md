Weather Info API

This API provides weather information based on pincode and date.

Parameters:
- pincode : The pincode for which you want to fetch the weather information. (e.g., 411014)
- for_date: The date for which you want the weather information in the format YYYY-MM-DD. (e.g., 2020-10-15)
  
Endpoint:
/api/v1/weatherInfo

Example Request
GET http://localhost:8080/api/v1/weatherInfo?pincode=411014&for_date=2020-10-15

