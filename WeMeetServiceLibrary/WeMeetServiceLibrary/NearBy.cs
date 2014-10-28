using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;

namespace WeMeetServiceLibrary
{
    [DataContract]
    public class NearBy
    {
        [DataMember]
        public string PhoneNumber;

        [DataMember]
        public Location Location;

        [DataMember]
        public double Distance;
    }
}
