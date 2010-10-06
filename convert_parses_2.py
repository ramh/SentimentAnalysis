import sys
import string
import re

tag_trans = { "ARG0": "WHO", "TARGET": "WHAT",
              "ARGM-TMP": "WHEN", "ARGM-LOC": "WHERE",
              "ARGM-PRP": "WHY1",
              "ARGM-PNC": "WHY2", "ARGM-CAU": "WHY3"}
    
#---- this is the file from which the sentences are to be extracted ----#
lines = open(sys.argv[1]).readlines()

matcher = re.compile(r"\[(?P<tag>[^ ]*) (?P<chunk>[^\]]*)\]")

nums, nlines, ntagslist, ntagsnums = [], [], [], []
uniqtags, tagsdicts = [], [] #mod

for line in lines:
    stind, endind = 0, len(line)
    ntagsnum = 0
    begs, tags, chunks = [], [], []
    tagsdict = {} #mod
    nline = ""
    num, parse = line.split(':')
    nums += [int(num)]
    parse = parse.strip()
    cur = parse
    match = matcher.search(cur,stind,endind)
    while match != None:
        beg, tag, chunk, end = cur[0:match.span('tag')[0]-1], cur[match.span('tag')[0]:match.span('tag')[1]], cur[match.span('chunk')[0]:match.span('chunk')[1]], cur[match.span('chunk')[1]+1:endind]
        begs += [beg]
        tags += [tag]
        
        # modified code for assign 2
        if tag in tagsdict:
            tagsdict[tag] += 1
        else:
            tagsdict[tag] = 1
        if not tag in uniqtags:
            uniqtags += [tag]
        # end mod code
        
        chunks += [chunk]
        cur = end
        match = matcher.search(cur,0,len(cur))
    ntags = []
    for tag in tags:
        if tag in tag_trans:
            ntags += [tag_trans[tag]]
        else:
            ntags += [tag]
        ntagsnum += 1
    for i in range(len(begs)):
        if ntags[i] != None:
            nline += begs[i] + "[%s %s]" % (ntags[i].strip(), chunks[i].strip())
        else:
            nline += begs[i] + chunks[i]
    nline += end
    nlines += [nline]
    ntagslist += [ntags]
    ntagsnums += [ntagsnum]
    tagsdicts += [tagsdict] #mod

curlinenum = 0

print ",".join(uniqtags)
    
i = 0
while i < len(nums):
#    import pdb; pdb.set_trace()
    while nums[i] != curlinenum:
        #print "%d: (no parse)" % (curlinenum + 1) 
        print ""
        curlinenum += 1
    useind = i
    if i+1 != len(nums) and nums[i] != nums[i+1]:
        prline = nlines[i]
    else:
        j = i
        while j < len(nums) and nums[j] == nums[i]:
            j += 1
        cnums = ntagsnums[i:j]
        useind = cnums.index(max(cnums)) + i
        prline = nlines[useind]
        i = j - 1
    #print "%d: %s" % (curlinenum + 1, prline) 
    print ",".join(["%s:%d" % (tag, tagsdicts[useind][tag]) for tag in tagsdicts[useind]])
    curlinenum += 1
    i += 1
