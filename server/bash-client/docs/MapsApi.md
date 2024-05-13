# MapsApi

All URIs are relative to **

Method | HTTP request | Description
------------- | ------------- | -------------
[**createMap**](MapsApi.md#createMap) | **POST** /maps | Create a new Map
[**getMapById**](MapsApi.md#getMapById) | **GET** /maps/{mapId} | Find Map by ID
[**getMaps**](MapsApi.md#getMaps) | **GET** /maps/list | Find all the available maps


## **createMap**

Create a new Map

### Example
```bash
 createMap
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**Map**](Map.md) | Map data |

### Return type

(empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

## **getMapById**

Find Map by ID

Returns a single map

### Example
```bash
 getMapById mapId=value
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **mapId** | **integer** | ID of Map to return |

### Return type

[**Map**](Map.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not Applicable
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

## **getMaps**

Find all the available maps

### Example
```bash
 getMaps
```

### Parameters
This endpoint does not need any parameter.

### Return type

(empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not Applicable
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

