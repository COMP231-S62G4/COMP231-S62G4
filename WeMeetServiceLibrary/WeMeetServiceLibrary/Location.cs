using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel;
using System.Runtime.Serialization;

namespace WeMeetServiceLibrary
{
    [DataContract]
    public class Location
    {
        [DataMember]
        public string Latitude;

        [DataMember]
        public string Longitude;

        [DataMember]
        public string Date;
    }
}
