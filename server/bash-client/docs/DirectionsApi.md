# DirectionsApi

All URIs are relative to **

Method | HTTP request | Description
------------- | ------------- | -------------
[**addDirection**](DirectionsApi.md#addDirection) | **POST** /direction | 
[**getDirectionsById**](DirectionsApi.md#getDirectionsById) | **GET** /direction/{mapId} | 


## **addDirection**



### Example
```bash
 addDirection
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**Direction**](Direction.md) | Direction data |

### Return type

(empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

## **getDirectionsById**



### Example
```bash
 getDirectionsById mapId=value
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **mapId** | **integer** | ID of Directions to return |

### Return type

[**Map**](Map.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not Applicable
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

