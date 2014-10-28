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
        [WebInvoke(UriTemplate = "CreateGroup/{phoneNumber}/{groupName}", Method = "GET")]
        void CreateGroup(string phoneNumber, String groupName);

        [OperationContract]
        [WebInvoke(UriTemplate = "RenameGroup/{phoneNumber}/{groupName}", Method = "GET")]
        void RenameGroup(string phoneNumber, String groupName);

        [OperationContract]
        [WebInvoke(UriTemplate = "AddGroupMember/{requesterPhoneNumber}/{groupName}/{phoneNumber}", Method = "GET")]
        void AddGroupMember(string requesterPhoneNumber, String groupName, string phoneNumber);

        [OperationContract]
        [WebInvoke(UriTemplate = "DeleteGroupMember/{requesterPhoneNumber}/{groupName}/{phoneNumber}", Method = "GET")]
        void DeleteGroupMember(string requesterPhoneNumber, String groupName, string phoneNumber);

        [OperationContract]
        [WebInvoke(UriTemplate = "DeleteGroup/{phoneNumber}/{groupName}", Method = "GET")]
        void DeleteGroup(string phoneNumber, String groupName);

        [OperationContract]
        [WebInvoke(UriTemplate = "GetGroups/{phoneNumber}", Method = "GET")]
        List<string> GetGroups(string phoneNumber);

        [OperationContract]
        [WebInvoke(UriTemplate = "GetGroupMembers/{phoneNumber}/{groupName}", Method = "GET")]
        List<string> GetGroupMembers(string phoneNumber, string groupName);

        [OperationContract]
        [WebInvoke(UriTemplate = "SendLocationSharingRequest/{fromPhoneNumber}/{toPhoneNumber}", Method = "GET")]
        bool SendLocationSharingRequest(string fromPhoneNumber, string toPhoneNumber);

        [OperationContract]
        [WebInvoke(UriTemplate = "AcceptLocationSharingRequest/{fromPhoneNumber}/{toPhoneNumber}", Method = "GET")]
        bool AcceptLocationSharingRequest(string fromPhoneNumber, string toPhoneNumber);

        [OperationContract]
        [WebInvoke(UriTemplate = "DeclineLocationSharingRequest/{fromPhoneNumber}/{toPhoneNumber}", Method = "GET")]
        bool DeclineLocationSharingRequest(string fromPhoneNumber, string toPhoneNumber);

        [OperationContract]
        [WebInvoke(UriTemplate = "GetFriendsNearBy/{phoneNumber}", Method = "GET")]
        List<NearBy> GetFriendsNearBy(string phoneNumber);
    }
}
