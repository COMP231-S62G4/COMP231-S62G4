using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;
using System.Threading.Tasks;

namespace WeMeetServiceLibrary
{
    [ServiceContract]
    public interface IWeMeetService
    {
        [OperationContract]
        [WebInvoke(UriTemplate = "Register/{phoneNumber}", Method="GET")]
        bool RegisterPhoneNumber(string phoneNumber);

        [OperationContract]
        [WebInvoke(UriTemplate = "IsRegistered/{phoneNumber}", Method = "GET")]
        bool IsRegisteredPhoneNumber(string phoneNumber);

        [OperationContract]
        [WebInvoke(UriTemplate = "UnRegister/{phoneNumber}", Method = "GET")]
        bool UnRegisterPhoneNumber(string phoneNumber);

        [OperationContract]
        [WebInvoke(UriTemplate = "UpdateLocation/{phoneNumber}/{latitude}/{longitude}",  Method="GET")]
        bool UpdateLocation(string phoneNumber, string latitude, string longitude);

        [OperationContract]
        [WebInvoke(UriTemplate = "GetLocation/{requesterPhoneNumber}/{phoneNumber}", Method = "GET")]
        Location GetLocation(string requesterPhoneNumber, string phoneNumber);


        [OperationContract]
        [WebInvoke(UriTemplate = "GetSharedLocationList/{phoneNumber}", Method = "GET")]
        string GetSharedLocationList(string phoneNumber);

        [OperationContract]
        [WebInvoke(UriTemplate = "IsRequestSentTo/{fromPhoneNumber}/{toPhoneNumber}", Method = "GET")]
        bool IsRequestSentTo(string fromPhoneNumber, string toPhoneNumber);

        [OperationContract]
        [WebInvoke(UriTemplate = "GetLocationRequests/{phoneNumber}", Method = "GET")]
        List<string> GetLocationRequests(string phoneNumber);

        [OperationContract]
        [WebInvoke(UriTemplate = "SendLocationSharingRequest/{fromPhoneNumber}/{toPhoneNumber}", Method = "GET")]
        bool SendLocationSharingRequest(string fromPhoneNumber, string toPhoneNumber);

        [OperationContract]
        [WebInvoke(UriTemplate = "AcceptLocationSharingRequest/{fromPhoneNumber}/{toPhoneNumber}", Method = "GET")]
        bool AcceptLocationSharingRequest(string fromPhoneNumber, string toPhoneNumber);

        [OperationContract]
        [WebInvoke(UriTemplate = "DeleteLocationSharingRequest/{fromPhoneNumber}/{toPhoneNumber}", Method = "GET")]
        bool DeleteLocationSharingRequest(string fromPhoneNumber, string toPhoneNumber);

        [OperationContract]
        [WebInvoke(UriTemplate = "GetFriendsNearBy/{phoneNumber}", Method = "GET")]
        List<NearBy> GetFriendsNearBy(string phoneNumber);
    }
}
